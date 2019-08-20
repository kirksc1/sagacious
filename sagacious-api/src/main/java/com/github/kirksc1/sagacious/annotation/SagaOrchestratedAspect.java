package com.github.kirksc1.sagacious.annotation;

import com.github.kirksc1.sagacious.SagaIdentifier;
import com.github.kirksc1.sagacious.SagaManager;
import com.github.kirksc1.sagacious.context.SagaContext;
import com.github.kirksc1.sagacious.context.SagaContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * SagaOrchestratedAspect is an aspect that operates around methods annotated with @{@link SagaOrchestrated} and
 * orchestrates the execution of a saga.
 */
@Aspect
@Slf4j
public class SagaOrchestratedAspect implements Ordered {

    /**
     * The default order for the aspect.
     */
    public static final int DEFAULT_ORDER = 0;

    private final ApplicationContext context;
    private final int order;

    /**
     * Construct a new instance with the provided ApplicationContext.
     * @param context The spring ApplicationContext.
     */
    public SagaOrchestratedAspect(ApplicationContext context) {
        this(context, DEFAULT_ORDER);
    }

    /**
     * Construct a new instance with the provided ApplicationContext.
     * @param context The spring ApplicationContext.
     * @param order The Ordered order governing the order of aspect execution.
     */
    public SagaOrchestratedAspect(ApplicationContext context, int order) {
        Assert.notNull(context, "The ApplicationContext provided is null");

        this.context = context;
        this.order = order;
    }

    /**
     * Apply a saga around the method execution.
     * @param joinPoint The ProceedingJoinPoint for the method call.
     * @return The return object of the method invocation.
     * @throws Throwable An exception that occurs during execution.
     */
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
                log.debug("Saga created with ID={}", sagaId);

                SagaContext sagaContext = new SagaContext(sagaManager, sagaId);
                SagaContextHolder.setSagaContext(sagaContext);

                Object retVal = joinPoint.proceed();
                sagaManager.completeSaga(sagaId);
                log.debug("Saga {} completed", sagaId);
                return retVal;
            } catch (Exception e) {
                sagaManager.failSaga(sagaId);
                log.debug("Saga {} failed", sagaId);
                throw e;
            } finally {
                SagaContextHolder.resetSagaContext();
            }
        } else {
            throw new IllegalStateException("The SagaOrchestrated could not be located");
        }
    }

    /**
     * Find the SagaOrchestrated annotation from the provided ProceedingJoinPoint.
     * @param joinPoint The ProceedingJoinPoint for a method invocation.
     * @return The SagaOrchestrated annotation if found, otherwise null.
     */
    private SagaOrchestrated findSagaOrchestrated(ProceedingJoinPoint joinPoint) {
        SagaOrchestrated retVal = null;

        if (joinPoint.getSignature() instanceof MethodSignature) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            retVal = method.getAnnotation(SagaOrchestrated.class);
        }

        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return order;
    }
}
