package com.github.kirksc1.sagacious.server;

import com.github.kirksc1.sagacious.*;
import com.github.kirksc1.sagacious.repository.SagaRepository;
import com.github.kirksc1.sagacious.server.valid.ValidationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * AddParticipantController is a Spring RestController that exposes the Add Participant endpoint.
 */
@RestController
@ConditionalOnProperty(name = "sagacious.server.endpoints.add-participant.enabled", havingValue = "true", matchIfMissing = true)
public class AddParticipantController {

    private final CompensatingActionDefinitionAssembler actionDefinitionAssembler;
    private final SagaManager sagaManager;
    private final SagaRepository sagaRepository;
    private final SagaAssembler sagaAssembler;

    /**
     * Construct a new AddParticipantController with the provided SagaManager and assembler.
     * @param sagaManager A SagaManager for managing local sagas.
     * @param actionDefinitionAssembler An assembler for assembling CompensatingActionDefinitions.
     */
    public AddParticipantController(SagaManager sagaManager, SagaRepository sagaRepository, SagaAssembler sagaAssembler, CompensatingActionDefinitionAssembler actionDefinitionAssembler) {
        Assert.notNull(sagaManager, "The SagaManager provided is null");
        Assert.notNull(sagaRepository, "The SagaRepository provided is null");
        Assert.notNull(sagaAssembler, "The SagaAssembler provided is null");
        Assert.notNull(actionDefinitionAssembler, "The CompensatingActionDefinitionAssembler provided is null");

        this.sagaManager = sagaManager;
        this.sagaRepository = sagaRepository;
        this.sagaAssembler = sagaAssembler;
        this.actionDefinitionAssembler = actionDefinitionAssembler;
    }

    /**
     * Add the provided participant to the identified saga.
     * @param sagaId The saga identifier to which the participant is to be added.
     * @param participant
     * @param result BindingResult containing any Spring validation errors.
     */
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "${sagacious.server.endpoints.web.base-path:}/sagas/{sagaId}/participants", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public SagaResource addParticipant(@PathVariable("sagaId") String sagaId, @Valid @RequestBody ParticipantResource participant, BindingResult result) {
        if (result.hasErrors()) {
            throw new ValidationException(result.getAllErrors());
        }

        SagaIdentifier sagaIdentifier = new SagaIdentifier(sagaId);
        ParticipantIdentifier participantIdentifier = new ParticipantIdentifier(participant.getIdentifier());
        CompensatingActionDefinition actionDefinition = actionDefinitionAssembler.toCompensatingActionDefinition(participant.getAction());

        try {
            sagaManager.addParticipant(sagaIdentifier, participantIdentifier, actionDefinition);
            return sagaRepository.findById(sagaId)
                    .map(saga -> sagaAssembler.toResource(saga))
                    .orElseThrow(() -> new ResourceNotFoundException());
        } catch (SagaNotFoundException e) {
            throw new ResourceNotFoundException();
        }
    }
}
