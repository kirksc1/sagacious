package com.github.kirksc1.sagacious.action;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.annotation.Executable;

/**
 * CompensatingActionDefinitionMatcher defines the responsibilities of classes that can determine
 * if a provided {@link CompensatingActionDefinition} matches teh {@link Executable} specification.
 */
public interface CompensatingActionDefinitionMatcher {

    /**
     * Check if the provided {@link Executable} specification can execute the provided {@link CompensatingActionDefinition}.
     * @param specification The specification.
     * @param instance The CompensatingActionDefinition being evaluated.
     * @return True if the {@link Executable} matches the definition, otherwise false.
     */
    boolean matches(Executable specification, CompensatingActionDefinition instance);
}
