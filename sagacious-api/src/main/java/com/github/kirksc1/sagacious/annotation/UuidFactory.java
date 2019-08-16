package com.github.kirksc1.sagacious.annotation;

import java.util.UUID;

public class UuidFactory implements IdentifierFactory {
    @Override
    public String buildIdentifier() {
        return UUID.randomUUID().toString();
    }
}
