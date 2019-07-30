package com.github.kirksc1.sagacious;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ParticipantIdentifier {

    @NonNull
    private final String identifier;

    public String toString() {
        return identifier;
    }
}
