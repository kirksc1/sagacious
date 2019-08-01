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
public class SagaParticipantAspect {

    @NonNull
    private ApplicationContext context;

    @Around("@annotation(com.github.kirksc1.sagacious.SagaParticipant)")
    public Object addParticipant(ProceedingJoinPoint joinPoint) throws Throwable {
        Object retVal = joinPoint.proceed();

        SagaContext sagaContext = SagaContextHolder.getSagaContext();
        if (sagaContext != null) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            SagaParticipant sagaParticipant = method.getAnnotation(SagaParticipant.class);
            Object factoryBeanObj = context.getBean(sagaParticipant.actionDefinitionFactory());
            if (factoryBeanObj instanceof CompensatingActionDefinitionFactory) {
                CompensatingActionDefinitionFactory factory = (CompensatingActionDefinitionFactory) factoryBeanObj;
                CompensatingActionDefinition definition = factory.buildDefinition(retVal);

                ParticipantIdentifier participantIdentifier = new ParticipantIdentifier(UUID.randomUUID().toString());

                sagaContext.getSagaManager().addParticipant(sagaContext.getIdentifier(), participantIdentifier, definition);
            } else {
                throw new IllegalStateException("The actionDefinitionFactory bean defined is not of type CompensatingActionDefinitionFactory");
            }
        }

        return retVal;
    }
}
