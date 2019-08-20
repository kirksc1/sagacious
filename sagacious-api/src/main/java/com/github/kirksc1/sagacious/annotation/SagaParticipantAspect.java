package com.github.kirksc1.sagacious.annotation;

import com.github.kirksc1.sagacious.CompensatingActionDefinition;
import com.github.kirksc1.sagacious.ParticipantIdentifier;
import com.github.kirksc1.sagacious.action.CompensatingActionDefinitionFactory;
import com.github.kirksc1.sagacious.context.SagaContext;
import com.github.kirksc1.sagacious.context.SagaContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * SagaParticipantAspect is an aspect that operates around methods annotated with @{@link SagaParticipant} and
 * adds a participant to the current saga.
 */
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

        Object retVal = joinPoint.proceed();

        if (!participantAdded && sagaContext != null) {
            addParticipant(sagaContext, method, retVal);
        }

        return retVal;
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
