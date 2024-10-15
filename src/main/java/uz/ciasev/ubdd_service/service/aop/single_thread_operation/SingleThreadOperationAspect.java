package uz.ciasev.ubdd_service.service.aop.single_thread_operation;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import uz.ciasev.ubdd_service.entity.single_thread_operation.SingleThreadOperationLock;
import uz.ciasev.ubdd_service.entity.single_thread_operation.SingleThreadOperationTypeAlias;
import uz.ciasev.ubdd_service.exception.implementation.ImplementationException;
import uz.ciasev.ubdd_service.repository.single_thread_operation.SingleThreadOperationRepository;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class SingleThreadOperationAspect {
    private final SingleThreadOperationRepository singleThreadOperationRepository;

    @Pointcut("@annotation(sto)")
    public void callAt(SingleThreadOperation sto) {
    }

    @Around("callAt(sto)")
    public Object around(ProceedingJoinPoint joinPoint, SingleThreadOperation sto) throws Throwable {

        Long resourceId = getResourceId(joinPoint);
        SingleThreadOperationTypeAlias operationType = sto.type();

        SingleThreadOperationLock operationLock = new SingleThreadOperationLock(resourceId, operationType);

        singleThreadOperationRepository.saveAndFlush(operationLock);

        try {
            return joinPoint.proceed();
        } finally {
            singleThreadOperationRepository.delete(operationLock);
        }
    }

    private Long getResourceId(ProceedingJoinPoint joinPoint) {
        Object resourceId = null;

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            for (Annotation paramAnnotation : parameterAnnotations[argIndex]) {
                if (paramAnnotation instanceof SingleThreadOperationResource) {
                     resourceId = args[argIndex];
                     break;
                }
            }
        }

        if (resourceId instanceof Long) {
            return (Long) resourceId;
        } else {
            throw new ImplementationException(method.getName() + " has no parameter with @SingleThreadOperationResource annotation" +
                    " or the annotated parameter is not of type Long");
        }
    }
}
