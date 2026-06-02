package com.sprint.mission.discodeit.logging;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.sprint.mission.discodeit.service..*.*(..))")
    public void serviceLayerPointcut() {}

    @Pointcut("execution(* com.sprint.mission.discodeit.controller..*.*(..))")
    public void controllerLayerPointcut() {}

    @Before("serviceLayerPointcut() || controllerLayerPointcut()")
    public void logBefore(JoinPoint joinPoint){
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.debug("==> {}.{}({})", className, methodName, Arrays.toString(args));
    }

    @AfterReturning(pointcut = "serviceLayerPointcut() || controllerLayerPointcut()",
            returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result){
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.debug("<== {}.{}({}), return {}", className, methodName, Arrays.toString(args), result);
    }

    @AfterThrowing(pointcut = "serviceLayerPointcut() || controllerLayerPointcut()",
            throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Exception e){
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String exceptionName = e.getClass().getSimpleName();

        log.error("<== {}.{}({}), exceptionName={}, message={}",
                className, methodName, Arrays.toString(args), exceptionName, e.getMessage(), e);
    }

    // 실행시간 측정하여 시간 오버된 경우 경고 남기기!!
    @Around("serviceLayerPointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        StopWatch watch = new StopWatch();
        watch.start();
        try {
            Object result = joinPoint.proceed(); // 대상 메소드 실행코드!
            watch.stop();
            long executionTime = watch.getTotalTimeMillis();

            log.info("{}.{} 실행시간 : {}ms", className, methodName, executionTime);

            // 성능 경고(1초)
            if (executionTime > 5000){
                log.warn("[warn!] {}.{} 실행시간이 {}ms로 느립니다. 성능 최적화가 필요합니다!"
                            ,className, methodName, executionTime);
            }
            return result;
        } catch (Throwable throwable) {
            watch.stop();
            long executionTime = watch.getTotalTimeMillis();
            log.error("{}.{} 실행 실패!! - 실행시간 : {}ms", className, methodName, executionTime);
            throw throwable;
        }
    }
}
