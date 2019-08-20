package com.github.kirksc1.sagacious.action;

import com.github.kirksc1.sagacious.repository.Saga;

/**
 * CompensatingActionStrategy defines the responsibilities for a strategy for executing a saga's participants'
 * compensating actions.
 */
public interface CompensatingActionStrategy {

    /**
     * Execute the compensating actions for the provided saga.
     * @param saga The saga for which the compensating actions are to be executed.
     */
    void performCompensatingActions(Saga saga);

}
