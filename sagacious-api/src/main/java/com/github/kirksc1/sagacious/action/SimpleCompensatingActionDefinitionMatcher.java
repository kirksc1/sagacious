package com.github.kirksc1.sagacious.action;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.annotation.Executable;

import java.net.URI;
import java.util.Optional;

/**
 * SimpleCompensatingActionDefinitionMatcher is a simple, concrete implementation of the
 * {@link CompensatingActionDefinitionMatcher} interface.
 */
public class SimpleCompensatingActionDefinitionMatcher implements CompensatingActionDefinitionMatcher {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(Executable specification, CompensatingActionDefinition instance) {
        boolean retVal = true;

        URI uri = Optional.ofNullable(instance.getUri())
                .map(URI::create)
                .orElse(null);
        if (specification.scheme().length() > 0) {
            retVal = uri != null && uri.getScheme().equalsIgnoreCase(specification.scheme());
        }

        return retVal;
    }
}
