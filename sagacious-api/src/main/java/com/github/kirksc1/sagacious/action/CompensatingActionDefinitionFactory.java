package com.github.kirksc1.sagacious.action;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;

public interface CompensatingActionDefinitionFactory<T> {

    CompensatingActionDefinition buildDefinition(T item);
}
