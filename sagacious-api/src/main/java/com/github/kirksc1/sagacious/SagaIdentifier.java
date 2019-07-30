package com.github.kirksc1.sagacious;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class SagaIdentifier {

    @NonNull
    private final String identifier;

    public String toString() {
        return identifier;
    }
}
