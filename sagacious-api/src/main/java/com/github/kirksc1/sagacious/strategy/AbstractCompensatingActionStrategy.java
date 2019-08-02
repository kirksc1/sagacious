package com.github.kirksc1.sagacious.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kirksc1.sagacious.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractCompensatingActionStrategy implements CompensatingActionStrategy {

    @NonNull
    private final CrudRepository<Saga, String> repository;

    @NonNull
    private final CompensatingActionManager manager;

    @NonNull
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void failParticipantIfNotAlreadyFailed(String sagaIdentifier, String participantIdentifier) {
        repository.findById(sagaIdentifier)
                .ifPresent(saga -> {
                    Optional.ofNullable(saga.getParticipants())
                            .ifPresent(participants ->  participants.stream()
                                    .filter(participant -> participant.getIdentifier().equals(participantIdentifier))
                                    .filter(participant -> !participant.getFailCompleted())
                                    .findFirst()
                                    .ifPresent(participant -> participant.setFailCompleted(performCompensatingAction(convert(participant.getActionDefinition()))))
                            );
                    repository.save(saga);
                });
    }

    protected boolean performCompensatingAction(CompensatingActionDefinition compensatingActionDefinition) {
        return manager.execute(compensatingActionDefinition);
    }

    protected CompensatingActionDefinition convert(String str) {
        try {
            return objectMapper.readValue(str, CompensatingActionDefinition.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to deserialize the JSON into a CompensatingActionDefinition", e);
        }
    }
}
