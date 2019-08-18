package com.github.kirksc1.sagacious.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.repository.Saga;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Optional;

/**
 * AbstractCompensatingActionStrategy provides basic functionality for concrete implementations of
 * the {@link CompensatingActionStrategy} interface.
 */
public abstract class AbstractCompensatingActionStrategy implements CompensatingActionStrategy {

    private final CrudRepository<Saga, String> repository;
    private final CompensatingActionManager manager;
    private final ObjectMapper objectMapper;

    /**
     * Construct a new instance.
     * @param repository A Repository for reading Saga entities.
     * @param manager The CompensatingActionManager that identifies the appropriate {@link CompensatingActionExecutor}
     * instance and executes the compensating action.
     * @param objectMapper An ObjectMapper to serialize/deserialize {@link CompensatingActionDefinition} instances
     * to/from Strings.
     */
    public AbstractCompensatingActionStrategy(CrudRepository<Saga, String> repository, CompensatingActionManager manager, ObjectMapper objectMapper) {
        Assert.notNull(repository, "The repository provided is null");
        Assert.notNull(manager, "The CompensatingActionManager provided is null");
        Assert.notNull(objectMapper, "The ObjectMapper provided is null");

        this.repository = repository;
        this.manager = manager;
        this.objectMapper = objectMapper;
    }

    /**
     * Execute the specified participant's compensating action and fail the step if not already failed.
     * <p>This method operates within a transactional context that requires a new transaction.</p>
     * @param sagaIdentifier The unique identifier for the saga.
     * @param participantIdentifier The unique identifier for the saga participant.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void failParticipantIfNotAlreadyFailed(String sagaIdentifier, String participantIdentifier) {
        repository.findById(sagaIdentifier)
                .ifPresent(saga -> {
                    Optional.ofNullable(saga.getParticipants())
                            .ifPresent(participants ->  participants.stream()
                                    .filter(participant -> participant.getIdentifier().equals(participantIdentifier))
                                    .filter(participant -> !participant.getFailCompleted())
                                    .findFirst()
                                    .ifPresent(participant -> participant.setFailCompleted(performCompensatingAction(deserialize(participant.getActionDefinition()))))
                            );
                    repository.save(saga);
                });
    }

    /**
     * Perform the compensating action provided.
     * @param compensatingActionDefinition The definition of the compensating action to be executed.
     * @return True if the compensating action was successful, othewise false.
     */
    protected boolean performCompensatingAction(CompensatingActionDefinition compensatingActionDefinition) {
        return manager.execute(compensatingActionDefinition);
    }

    /**
     * Deserialize the provided String into a CompensatingActionDefinition instance.
     * @param str The String to deserialize.
     * @return The CompensatingActionDefinition.
     */
    protected CompensatingActionDefinition deserialize(String str) {
        try {
            return objectMapper.readValue(str, CompensatingActionDefinition.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to deserialize the JSON into a CompensatingActionDefinition", e);
        }
    }
}
