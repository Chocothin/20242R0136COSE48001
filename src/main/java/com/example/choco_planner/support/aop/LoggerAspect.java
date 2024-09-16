package com.example.choco_planner.support.aop;

import com.example.choco_planner.common.exception.enums.LoggingType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;


@Aspect
@Component
public class LoggerAspect {

    // Pointcut for controller methods
    @Pointcut("execution(* com.example.choco_planner.controller.*.*(..))")
    public void controllerPointcut() {}

    // Logging for controller methods
    @Around("controllerPointcut()")
    public Object controllerLogging(ProceedingJoinPoint pjp) throws Throwable {
        Logger logger = LoggerFactory.getLogger(pjp.getTarget().getClass());
        StopWatch stopWatch = start();

        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                logger.info(startMessage(LoggingType.API, requestAttributes.getRequest().getMethod() + " " + requestAttributes.getRequest().getRequestURI(),
                        Arrays.stream(pjp.getArgs()).collect(Collectors.toList())));
            } else {
                logger.info(startMessage(LoggingType.API, pjp.getSignature().toShortString(), Arrays.stream(pjp.getArgs()).collect(Collectors.toList())));
            }

            Object returnObj = pjp.proceed();
            logger.info(endMessage(LoggingType.API, stopWatch, processResultForLogging(returnObj)));

            return returnObj;
        } catch (Throwable e) {
            logger.error(errorMessage(LoggingType.API, stopWatch, e));
            throw e;
        }
    }

    // Process the result for logging
    private Object processResultForLogging(Object result) {
        if (result instanceof ResponseEntity) {
            return processResultForLogging(((ResponseEntity<?>) result).getBody());
        }
        return result;
    }

    // Start message format
    private String startMessage(LoggingType type, String target, Object args) {
        return String.format("[%s START] %s %s", type, target, args);
    }

    // End message format
    private String endMessage(LoggingType type, StopWatch stopWatch, Object returnObj) {
        return String.format("[%s END][ELAPSE: %.2f ms] %s", type, getElapse(stopWatch), returnObj);
    }

    // Error message format
    private String errorMessage(LoggingType type, StopWatch stopWatch, Throwable e) {
        return String.format("[%s ERROR][ELAPSE: %.2f ms] %s %s", type, getElapse(stopWatch), e.getClass().getSimpleName(), e.getMessage());
    }

    // Start the stopwatch
    private StopWatch start() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        return stopWatch;
    }

    // Get elapsed time in seconds
    private double getElapse(StopWatch stopWatch) {
        stopWatch.stop();
        return stopWatch.getTotalTimeMillis() / 1000.0;
    }
}
