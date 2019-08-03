package com.github.kirksc1.sagacious;

import java.net.URI;
import java.util.Optional;

public class SimpleCompensatingActionDefinitionMatcher implements CompensatingActionDefinitionMatcher {
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
