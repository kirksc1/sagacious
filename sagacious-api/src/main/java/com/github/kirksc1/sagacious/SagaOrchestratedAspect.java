package com.github.kirksc1.sagacious;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.UUID;

@Aspect
@RequiredArgsConstructor
public class SagaOrchestratedAspect {

    @NonNull
    private final ApplicationContext context;

    @Around("@annotation(com.github.kirksc1.sagacious.SagaOrchestrated)")
    public Object applySaga(ProceedingJoinPoint joinPoint) throws Throwable {
        SagaIdentifier sagaId = new SagaIdentifier(UUID.randomUUID().toString());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SagaOrchestrated sagaOrchestrated = method.getAnnotation(SagaOrchestrated.class);
        SagaManager sagaManager = context.getBean(sagaOrchestrated.sagaManager(), SagaManager.class);

        sagaManager.createSaga(sagaId);

        SagaContext sagaContext = new SagaContext(sagaManager, sagaId);
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
