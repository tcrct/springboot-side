package com.springbootside.duang.common.proxy;

import com.springbootside.duang.common.utils.SettingKit;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

@Aspect
@Configuration
public class AspectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AspectService.class);

    private ThreadLocal<Long> time = new ThreadLocal<>();

    private static final String executionString = "execution(* com.zat.coupon.dao.*Dao.*(..))";

    /* 定义一个切入点 */
    @Pointcut(executionString)
    public void doPointCut() {
        LOGGER.warn("pointCut");
    }

    /* 通过连接点切入 */
    @Before(executionString)
    public void doBefore() {
        LOGGER.warn("doBefore()");
        time.set(System.currentTimeMillis());
    }

    @AfterReturning(executionString)
    public void doAfterReturning(JoinPoint joinPoint) {
        LOGGER.warn("doAfterReturning(joinPoint) {}, time used={}", joinPoint.getSignature(),
                System.currentTimeMillis() - time.get());
    }

    @Around(executionString)
    public Object doAround(ProceedingJoinPoint joinPoint) {
        LOGGER.warn("AOP @Around start");
        try {
            LOGGER.warn("{}", joinPoint);
            System.out.println(joinPoint.getTarget().toString());
            System.out.println(joinPoint.getTarget().getClass().getName());
            System.out.println(joinPoint.getKind());
            System.out.println(joinPoint.getSignature().getDeclaringTypeName());
            Object[] args = joinPoint.getArgs();
            for (Object obj : args) {
                System.out.println(obj.getClass()+"                 "+obj);
            }
            Signature signature = joinPoint.getSignature();
            MethodSignature methodSignature = (MethodSignature)signature;
            Method targetMethod = methodSignature.getMethod();
            System.out.println("classname:" + targetMethod.getDeclaringClass().getName());
            Method realMethod = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), targetMethod.getParameterTypes());
            System.out.println(realMethod.getName()+"                 "+realMethod);
            Object obj = joinPoint.proceed();
            LOGGER.warn("AOP @Around end");
            return obj;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
