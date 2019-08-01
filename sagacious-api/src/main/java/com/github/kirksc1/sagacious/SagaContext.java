package com.github.kirksc1.sagacious;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class SagaContext {

    @NonNull
    private SagaManager sagaManager;
    @NonNull
    private SagaIdentifier identifier;

}
