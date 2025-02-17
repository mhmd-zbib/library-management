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
    public Object logOperation(ProceedingJoinPoint joinPoint, LoggableOperation loggableOperation) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        logger.info("Starting {} - {}: {}.{}() with arguments: {}",
                loggableOperation.operationType(),
                loggableOperation.description(),
                className,
                methodName,
                args);

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            logger.info("Completed {} in {} ms: {}.{}()",
                    loggableOperation.operationType(),
                    executionTime,
                    className,
                    methodName);

            return result;
        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Failed {} after {} ms: {}.{}() - Error: {}",
                    loggableOperation.operationType(),
                    executionTime,
                    className,
                    methodName,
                    ex.getMessage(),
                    ex);

            throw ex;
        }
    }
} 