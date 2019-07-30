package com.github.kirksc1.sagacious;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.UUID;

@Aspect
@RequiredArgsConstructor
public class SagaOrchestratedAspect {

    @NonNull
    private SagaManager sagaManager;

    @Around("@annotation(com.github.kirksc1.sagacious.SagaOrchestrated)")
    public Object applySaga(ProceedingJoinPoint joinPoint) throws Throwable {
        SagaIdentifier sagaId = new SagaIdentifier(UUID.randomUUID().toString());

        sagaManager.createSaga(sagaId);

        SagaContext sagaContext = new SagaContext();
        sagaContext.setIdentifier(sagaId);
        SagaContextHolder.setSagaContext(sagaContext);

        try {
            Object retVal = joinPoint.proceed();
            sagaManager.completeSaga(sagaId);
            return retVal;
        } catch (Exception e) {
            sagaManager.failSaga(sagaId);
            throw e;
        }
    }
}
