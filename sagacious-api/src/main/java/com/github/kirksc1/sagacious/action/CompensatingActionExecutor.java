package com.github.kirksc1.sagacious.action;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;

public interface CompensatingActionExecutor {

    boolean execute(CompensatingActionDefinition definition);

}
