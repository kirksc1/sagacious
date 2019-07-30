package com.github.kirksc1.sagacious;

public interface CompensatingActionDefinitionFactory<T> {

    CompensatingActionDefinition buildDefinition(T item);
}
