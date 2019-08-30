package com.github.kirksc1.sagacious.annotation;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.ParticipantIdentifier;
import com.github.kirksc1.sagacious.action.CompensatingActionDefinitionFactory;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * SagaParticipantAspect is an aspect that operates around methods annotated with @{@link SagaParticipant} and
 * adds a participant to the current saga.
 */
@Slf4j
@Aspect
public class SagaParticipantAspect implements Ordered {

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
    public SagaParticipantAspect(ApplicationContext context) {
        this(context, DEFAULT_ORDER);
    }

    /**
     * Construct a new instance with the provided ApplicationContext.
     * @param context The spring ApplicationContext.
     * @param order The Ordered order governing the order of aspect execution.
     */
    public SagaParticipantAspect(ApplicationContext context, int order) {
        Assert.notNull(context, "The ApplicationContext provided is null");

        this.context = context;
        this.order = order;
    }

    /**
     * Add a participant to the saga for the method execution.
     * @param joinPoint The ProceedingJoinPoint for the method call.
     * @return The return object of the method invocation.
     * @throws Throwable An exception that occurs during execution.
     */
    @Around("@annotation(com.github.kirksc1.sagacious.annotation.SagaParticipant)")
    public Object addParticipant(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean participantAdded = false;

        SagaContext sagaContext = SagaContextHolder.getSagaContext();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        if (sagaContext != null) {
            Object participantData = findParticipantData(joinPoint, method);

            if (participantData != null) {
                addParticipant(sagaContext, method, participantData);
                participantAdded = true;
            }
        }

        Object retVal = null;
        try {
            retVal = joinPoint.proceed();
        } catch (Throwable e) {
            if (sagaContext != null) {
                handleThrowable(joinPoint, sagaContext, e);
            }
            throw e;
        }

        if (!participantAdded && sagaContext != null) {
            addParticipant(sagaContext, method, retVal);
        }

        return retVal;
    }

    /**
     * Process a Throwable for the participant when operating within a saga.
     * @param joinPoint The ProceedingJoinPoint for the method call.
     * @param sagaContext The current SagaContext.
     * @param e An exception that occurred during execution.
     */
    private void handleThrowable(ProceedingJoinPoint joinPoint, SagaContext sagaContext, Throwable e) {
        SagaParticipant sagaParticipant = findSagaParticipant(joinPoint);
        if (sagaParticipant != null) {
            if (sagaParticipant.autoFail() && buildExceptionManager(sagaParticipant).failOn(e)) {
                sagaContext.getSagaManager().failSaga(sagaContext.getIdentifier());
                log.debug("Saga {} failed", sagaContext.getIdentifier());
            }
        } else {
            throw new IllegalStateException("The SagaParticipant could not be located");
        }
    }

    /**
     * Find the SagaParticipant annotation from the provided ProceedingJoinPoint.
     * @param joinPoint The ProceedingJoinPoint for a method invocation.
     * @return The SagaParticipant annotation if found, otherwise null.
     */
    private SagaParticipant findSagaParticipant(ProceedingJoinPoint joinPoint) {
        SagaParticipant retVal = null;

        if (joinPoint.getSignature() instanceof MethodSignature) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            retVal = method.getAnnotation(SagaParticipant.class);
        }

        return retVal;
    }

    /**
     * Build an ExceptionManager.
     * @param annotation The SagaParticipant annotation.
     * @return A RuleBasedExceptionManager if rules are provided, otherwise a DefaultExceptionManager.
     */
    private ExceptionManager buildExceptionManager(SagaParticipant annotation) {
        ExceptionManager exceptionManager = new DefaultExceptionManager();

        List<ExceptionRule> rules = new ArrayList<>();

        for (Class<?> cls : annotation.failFor()) {
            rules.add(new ExceptionRule(cls, true));
        }
        for (String clsName : annotation.failForClassName()) {
            rules.add(new ExceptionRule(clsName, true));
        }

        for (Class<?> cls : annotation.noFailFor()) {
            rules.add(new ExceptionRule(cls, false));
        }
        for (String clsName : annotation.noFailForClassName()) {
            rules.add(new ExceptionRule(clsName, false));
        }

        if (!rules.isEmpty()) {
            exceptionManager = new RuleBasedExceptionManager(rules);
        }

        return exceptionManager;
    }

    /**
     * Find the ParticipantData within the provided method invocation.
     * @param joinPoint The ProceedingJoinPoint for the method call.
     * @param method The method for the method call.
     * @return The method parameter identified as the ParticipantData if found, otherwise null.
     */
    private Object findParticipantData(ProceedingJoinPoint joinPoint, Method method) {
        Object participantData = null;

        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i=0; i < annotations.length; i++) {
            for (int j=0; j < annotations[i].length; j++) {
                if (annotations[i][j].annotationType().equals(ParticipantData.class)) {
                    participantData = joinPoint.getArgs()[i];
                    break;
                }
            }
            if (participantData != null) {
                break;
            }
        }
        return participantData;
    }

    /**
     * Add the method as a participant to the saga.
     * @param sagaContext The current SagaContext.
     * @param method The method for the method call.
     * @param participantData The data that can be used to build the CompensatingActionDefinition via the
     * CompensatingActionDefinitionFactory.
     */
    private void addParticipant(SagaContext sagaContext, Method method, Object participantData) {
        SagaParticipant sagaParticipant = method.getAnnotation(SagaParticipant.class);
        Object factoryBeanObj = context.getBean(sagaParticipant.actionDefinitionFactory());
        if (factoryBeanObj instanceof CompensatingActionDefinitionFactory) {
            CompensatingActionDefinitionFactory factory = (CompensatingActionDefinitionFactory) factoryBeanObj;
            CompensatingActionDefinition definition = factory.buildDefinition(participantData);

            IdentifierFactory identifierFactory = context.getBean(sagaParticipant.identifierFactory(), IdentifierFactory.class);
            ParticipantIdentifier participantIdentifier = new ParticipantIdentifier(identifierFactory.buildIdentifier());

            sagaContext.getSagaManager().addParticipant(sagaContext.getIdentifier(), participantIdentifier, definition);
        } else {
            throw new IllegalStateException("The actionDefinitionFactory bean defined is not of type CompensatingActionDefinitionFactory");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return order;
    }
}
