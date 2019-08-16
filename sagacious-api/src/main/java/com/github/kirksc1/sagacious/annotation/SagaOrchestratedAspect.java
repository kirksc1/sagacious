package com.github.kirksc1.sagacious.annotation;

import com.github.kirksc1.sagacious.*;
import com.github.kirksc1.sagacious.context.SagaContext;
import com.github.kirksc1.sagacious.context.SagaContextHolder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;
import java.util.Optional;

@Aspect
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestratedAspect implements Ordered {

    public static final int DEFAULT_ORDER = 0;

    @NonNull
    private final ApplicationContext context;
    private final int order;

    public SagaOrchestratedAspect(ApplicationContext context) {
        this(context, DEFAULT_ORDER);
    }

    @Around("@annotation(com.github.kirksc1.sagacious.annotation.SagaOrchestrated)")
    public Object applySaga(ProceedingJoinPoint joinPoint) throws Throwable {
        SagaOrchestrated sagaOrchestrated = findSagaOrchestrated(joinPoint);
        if (sagaOrchestrated != null) {
            SagaManager sagaManager = Optional.ofNullable(context.getBean(sagaOrchestrated.sagaManager(), SagaManager.class))
                    .orElseThrow(() -> new IllegalStateException("Unable to locate the specified SagaManager bean for name=" + sagaOrchestrated.sagaManager()));

            IdentifierFactory identifierFactory = Optional.ofNullable(context.getBean(sagaOrchestrated.identifierFactory(), IdentifierFactory.class))
                    .orElseThrow(() -> new IllegalStateException("Unable to locate the specified IdentifierFactory bean for name=" + sagaOrchestrated.identifierFactory()));

            SagaIdentifier sagaId = new SagaIdentifier(identifierFactory.buildIdentifier());

            try {
                sagaManager.createSaga(sagaId);

                SagaContext sagaContext = new SagaContext(sagaManager, sagaId);
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
        } else {
            throw new IllegalStateException("The SagaOrchestrated could not be located");
        }
    }

    private SagaOrchestrated findSagaOrchestrated(ProceedingJoinPoint joinPoint) {
        SagaOrchestrated retVal = null;

        if (joinPoint.getSignature() instanceof MethodSignature) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            retVal = method.getAnnotation(SagaOrchestrated.class);
        }

        return retVal;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
