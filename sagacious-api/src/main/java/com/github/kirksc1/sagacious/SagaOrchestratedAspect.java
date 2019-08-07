package com.github.kirksc1.sagacious;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

@Aspect
@RequiredArgsConstructor
public class SagaOrchestratedAspect implements Ordered {

    public static final int DEFAULT_ORDER = 0;

    @NonNull
    private final ApplicationContext context;
    private final int order;

    public SagaOrchestratedAspect(ApplicationContext context) {
        this(context, DEFAULT_ORDER);
    }

    @Around("@annotation(com.github.kirksc1.sagacious.SagaOrchestrated)")
    public Object applySaga(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SagaOrchestrated sagaOrchestrated = method.getAnnotation(SagaOrchestrated.class);
        SagaManager sagaManager = context.getBean(sagaOrchestrated.sagaManager(), SagaManager.class);
        IdentifierFactory identifierFactory = context.getBean(sagaOrchestrated.identifierFactory(), IdentifierFactory.class);

        SagaIdentifier sagaId = new SagaIdentifier(identifierFactory.buildIdentifier());
        sagaManager.createSaga(sagaId);

        SagaContext sagaContext = new SagaContext(sagaManager, sagaId);

        try {
            SagaContextHolder.setSagaContext(sagaContext);

            Object retVal = joinPoint.proceed();
            sagaManager.completeSaga(sagaId);
            return retVal;
        } catch (Exception e) {
            sagaManager.failSaga(sagaId);
            throw e;
        } finally {
            SagaContextHolder.resetSagaContext();
        }
    }

    @Override
    public int getOrder() {
        return order;
    }
}
