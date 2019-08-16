package com.github.kirksc1.sagacious.context;

import com.github.kirksc1.sagacious.SagaIdentifier;
import com.github.kirksc1.sagacious.SagaManager;
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
