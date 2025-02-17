package dev.zbib.librarymanagement.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LogManager.getLogger(LoggingAspect.class);

    @Around("@annotation(loggableOperation)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LoggableOperation loggableOperation) throws Throwable {
        final long start = System.currentTimeMillis();
        final String methodName = joinPoint.getSignature().getName();
        final String className = joinPoint.getTarget().getClass().getSimpleName();
        
        try {
            logger.info("Executing {} in class {}", methodName, className);
            Object result = joinPoint.proceed();
            logger.info("Completed {} in {} ms. Operation type: {}",
                methodName, System.currentTimeMillis() - start, loggableOperation.operationType());
            return result;
        } catch (Exception e) {
            logger.error("Error in {} - {}: {}", className, methodName, e.getMessage());
            throw e;
        }
    }
} 