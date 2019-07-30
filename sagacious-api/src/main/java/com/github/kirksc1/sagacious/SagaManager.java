package com.github.kirksc1.sagacious;

/**
 * The SagaManager interface defines that contract for all concrete implementations.
 */
public interface SagaManager {

    /**
     * Create a new saga with the provided identifier.
     * @param sagaIdentifier The unique identifier for the saga.
     * @return True if the saga was successfully created, otherwise false.
     */
    boolean createSaga(SagaIdentifier sagaIdentifier);

    /**
     * Add a saga participant, with a compensating action definition, to an existing saga.
     * @param sagaIdentifier The unique identifier for the saga to which the participant should be added.
     * @param participantIdentifier The unique identifier for the saga participant.
     * @param compensatingAction The definition for the compensating action that should be executed should the saga fail.
     * @return True if the saga is still processing successfully, otherwise false.
     */
    boolean addParticipant(SagaIdentifier sagaIdentifier, ParticipantIdentifier participantIdentifier, CompensatingActionDefinition compensatingAction);

    /**
     * Fail the saga with the provided identifier.
     * @param sagaIdentifier The unique identifier for the saga.
     * @return True if the saga was set to failed, otherwise false.
     */
    boolean failSaga(SagaIdentifier sagaIdentifier);

    /**
     * Complete the saga with the provided identifier.
     * @param sagaIdentifier The unique identifier for the saga.
     * @return True if the saga was set to completed, otherwise false.
     */
    boolean completeSaga(SagaIdentifier sagaIdentifier);

    /**
     * Retrieve whether the saga with the provided identifier has been set to failed.
     * @param sagaIdentifier The unique identifier for the saga.
     * @return True if the saga is set to failed, otherwise, false.
     */
    boolean hasSagaFailed(SagaIdentifier sagaIdentifier);

    /**
     * Retrieve whether the saga with the provided identifier has been set to completed.
     * @param sagaIdentifier The unique identifier for the saga.
     * @return True if the saga is set to completed, otherwise, false.
     */
    boolean hasSagaCompleted(SagaIdentifier sagaIdentifier);
}
