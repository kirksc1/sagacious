package com.github.kirksc1.sagacious.action;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.annotation.Executable;

public interface CompensatingActionDefinitionMatcher {

    boolean matches(Executable specification, CompensatingActionDefinition instance);
}
