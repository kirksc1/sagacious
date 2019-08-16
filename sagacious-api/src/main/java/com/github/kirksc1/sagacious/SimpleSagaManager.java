package com.github.kirksc1.sagacious;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kirksc1.sagacious.action.CompensatingActionStrategy;
import com.github.kirksc1.sagacious.repository.Participant;
import com.github.kirksc1.sagacious.repository.Saga;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@RequiredArgsConstructor
public class SimpleSagaManager implements SagaManager {

    public static final String THE_SAGA_PROVIDED_WAS_NOT_FOUND = "The Saga provided was not found";
    public static final String THE_SAGA_IDENTIFIER_PROVIDED_IS_NULL = "The SagaIdentifier provided is null";

    @NonNull
    private final CrudRepository<Saga, String> repository;

    @NonNull
    private final CompensatingActionStrategy compensatingActionStrategy;

    @NonNull
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public boolean createSaga(SagaIdentifier sagaIdentifier) {
        Assert.notNull(sagaIdentifier, THE_SAGA_IDENTIFIER_PROVIDED_IS_NULL);

        Saga saga = new Saga();
        saga.setIdentifier(sagaIdentifier.toString());
        saga.setFailed(Boolean.FALSE);
        saga.setCompleted(Boolean.FALSE);

        repository.save(saga);
        return true;
    }

    @Override
    public boolean completeSaga(SagaIdentifier sagaIdentifier) {
        Assert.notNull(sagaIdentifier, THE_SAGA_IDENTIFIER_PROVIDED_IS_NULL);

        Saga saga = repository.findById(sagaIdentifier.toString())
                .orElseThrow(() -> new IllegalArgumentException(THE_SAGA_PROVIDED_WAS_NOT_FOUND));

        boolean retVal = false;

        if (!saga.isFailed()) {
            flagSagaForCompleted(sagaIdentifier.toString());
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean addParticipant(SagaIdentifier sagaIdentifier, ParticipantIdentifier participantIdentifier, CompensatingActionDefinition compensatingAction) {
        Assert.notNull(sagaIdentifier, THE_SAGA_IDENTIFIER_PROVIDED_IS_NULL);
        Assert.notNull(participantIdentifier, "The ParticipantIdentifier provided is null");
        Assert.notNull(compensatingAction, "The CompensatingActionDefinition provided is null");

        String compensatingActionStr;
        try {
            compensatingActionStr = objectMapper.writeValueAsString(compensatingAction);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("The CompensatingActionDefinition could not be written to JSON", e);
        }

        return repository.findById(sagaIdentifier.toString()).map(saga -> {
            if (saga.isFailed()) {
                compensatingActionStrategy.performCompensatingActions(saga);
            }

            Participant participant = new Participant();
            participant.setIdentifier(participantIdentifier.toString());
            participant.setActionDefinition(compensatingActionStr);
            participant.setFailCompleted(saga.isFailed());
            participant.setOrderIndex(saga.getParticipants().size() + 1);

            saga.getParticipants().add(participant);

            repository.save(saga);

            return !saga.isFailed();
        }).orElseThrow(() -> new IllegalArgumentException(THE_SAGA_PROVIDED_WAS_NOT_FOUND));
    }

    @Override
    public boolean failSaga(SagaIdentifier sagaIdentifier) {
        Assert.notNull(sagaIdentifier, THE_SAGA_IDENTIFIER_PROVIDED_IS_NULL);
        Saga saga = repository.findById(sagaIdentifier.toString())
                .orElseThrow(() -> new IllegalArgumentException(THE_SAGA_PROVIDED_WAS_NOT_FOUND));

        boolean retVal = false;
        if (!saga.isCompleted()) {
            flagSagaForFailure(sagaIdentifier.toString());
            retVal = true;

            compensatingActionStrategy.performCompensatingActions(saga);

            flagSagaForFailureCompletedIfAllParticipantsCompleted(sagaIdentifier.toString());
        }
        return retVal;
    }

    @Override
    public boolean hasSagaFailed(SagaIdentifier sagaIdentifier) {
        Assert.notNull(sagaIdentifier, THE_SAGA_IDENTIFIER_PROVIDED_IS_NULL);

        return repository.findById(sagaIdentifier.toString()).map(Saga::isFailed)
                .orElseThrow(() -> new IllegalArgumentException(THE_SAGA_PROVIDED_WAS_NOT_FOUND));
    }

    @Override
    public boolean hasSagaCompleted(SagaIdentifier sagaIdentifier) {
        Assert.notNull(sagaIdentifier, THE_SAGA_IDENTIFIER_PROVIDED_IS_NULL);

        return repository.findById(sagaIdentifier.toString()).map(Saga::isCompleted)
                .orElseThrow(() -> new IllegalArgumentException(THE_SAGA_PROVIDED_WAS_NOT_FOUND));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private boolean flagSagaForFailure(String identifier) {
        return repository.findById(identifier).map(saga -> {
            saga.setFailed(Boolean.TRUE);
            repository.save(saga);

            return Boolean.TRUE;
        }).orElse(Boolean.FALSE);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private boolean flagSagaForCompleted(String identifier) {
        return repository.findById(identifier).map(saga -> {
            saga.setCompleted(Boolean.TRUE);
            repository.save(saga);

            return Boolean.TRUE;
        }).orElse(Boolean.FALSE);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private boolean flagSagaForFailureCompletedIfAllParticipantsCompleted(String identifier) {
        return repository.findById(identifier).map(saga -> {
            boolean completed = true;

            for (Participant participant : saga.getParticipants()) {
                completed = completed && participant.getFailCompleted();
                if (!completed) {
                    break;
                }
            }

            saga.setCompleted(completed);
            repository.save(saga);

            return completed;
        }).orElse(Boolean.FALSE);
    }
}
