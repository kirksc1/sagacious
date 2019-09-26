package com.github.kirksc1.sagacious.server;

import com.github.kirksc1.sagacious.repository.Saga;

public class SagaAssembler {

    public SagaResource toResource(Saga saga) {
        SagaResource retVal = new SagaResource();
        retVal.setIdentifier(saga.getIdentifier());
        retVal.setCompleted(saga.isCompleted());
        retVal.setFailed(saga.isFailed());

        return retVal;
    }
}
