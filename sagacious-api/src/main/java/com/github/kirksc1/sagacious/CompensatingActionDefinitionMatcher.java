package com.github.kirksc1.sagacious;

public interface CompensatingActionDefinitionMatcher {

    boolean matches(Executable specification, CompensatingActionDefinition instance);
}
