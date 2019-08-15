package com.github.kirksc1.sagacious;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@RequiredArgsConstructor
public class SagaParticipantAspect implements Ordered {

    public static final int DEFAULT_ORDER = 0;

    @NonNull
    private final ApplicationContext context;
    private final int order;

    public SagaParticipantAspect(ApplicationContext context) {
        this(context, DEFAULT_ORDER);
    }

    @Around("@annotation(com.github.kirksc1.sagacious.SagaParticipant)")
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

    @Override
    public int getOrder() {
        return order;
    }
}
