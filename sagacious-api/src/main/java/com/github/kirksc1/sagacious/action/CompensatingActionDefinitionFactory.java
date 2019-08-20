package com.github.kirksc1.sagacious.action;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;

/**
 * CompensatingActionDefinitionFactory defines the responsibilities of classes that can build instances
 * of {@link CompensatingActionDefinition} using a single input.
 * @param <T> The type of used as input for building the {@link CompensatingActionDefinition} instance.
 */
public interface CompensatingActionDefinitionFactory<T> {

    /**
     * Build a new {@link CompensatingActionDefinition} from the information provided.
     * @param item An input parameter.
     * @return The definition.
     */
    CompensatingActionDefinition buildDefinition(T item);
}
