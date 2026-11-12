package dev.arunsamra.fileprocessor.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class MethodLoggingAspect {

    @Pointcut("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
    public void controllerMethods() {}

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void serviceMethods() {}

    @Around("execution(public * *(..)) && (controllerMethods() || serviceMethods())")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!log.isDebugEnabled()) {
            return joinPoint.proceed();
        }

        String method = joinPoint.getSignature().toLongString();

        log.debug("Method entered: method={}", method);

        long startedAt = System.nanoTime();
        String outcome = "returned";
        String exceptionType = null;

        try {
            return joinPoint.proceed();

        } catch (Throwable failure) {
            outcome = "threw";
            exceptionType = failure.getClass().getName();
            throw failure;

        } finally {
            double durationMs = (System.nanoTime() - startedAt) / 1000000.0;

            log.debug("Method existed: method={}, outcome={}, duration={}, exceptionType={}",
                    method, outcome, durationMs, exceptionType);
        }
    }
}
