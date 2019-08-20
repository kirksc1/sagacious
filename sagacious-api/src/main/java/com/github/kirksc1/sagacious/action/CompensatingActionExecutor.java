package com.github.kirksc1.sagacious.action;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;

/**
 * CompensatingActionExecutor defines the responsibilities for classes able to execute a compensating action
 * for a saga.
 */
public interface CompensatingActionExecutor {

    /**
     * Execute the provided CompensatingActionDefinition.
     * @param definition The CompensatingActionDefinition of the action to execute.
     * @return True if the compensating action was executed successfully, otherwise false.
     */
    boolean execute(CompensatingActionDefinition definition);

}
