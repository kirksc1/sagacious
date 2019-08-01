package com.github.kirksc1.sagacious.identifier;

import com.github.kirksc1.sagacious.IdentifierFactory;

import java.util.UUID;

public class UuidFactory implements IdentifierFactory {
    @Override
    public String buildIdentifier() {
        return UUID.randomUUID().toString();
    }
}
