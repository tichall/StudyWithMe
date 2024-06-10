package december.spring.studywithme.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;


@Slf4j
@Aspect
@Component
public class LogAspect {
    // 포인트컷 시그니처
    @Pointcut("execution(* december.spring.studywithme..*Service.*(..))")
    public void service() {}

    @Before("service()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        log.info("[Entering] {} with arguments: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @After("service()")
    public void doAfter(JoinPoint joinPoint) throws Throwable {
        log.info("[Exiting] {}", joinPoint.getSignature().getName());
    }

    @Around("service()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        log.info("[Starting] {} -> {}", signature.getDeclaringTypeName(), signature.getName());
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                log.info("[Exception] {} 발생 : {}", e.getClass().getName(), e.getMessage());
            } else {
                log.error("[Exception] {} 발생 : {} \n{}", e.getClass().getName(), e.getMessage(), e.getStackTrace());
            }
            throw e;
        }
        long finish = System.currentTimeMillis();
        long time = finish - start;
        log.info("[Completed] {} -> {} in {} ms with result: {}", signature.getDeclaringTypeName(), signature.getName(), time, result);
        return result;
    }
}
