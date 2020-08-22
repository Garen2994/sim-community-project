package com.garen.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {
    //1.定义切点
    //1-* 所有返回值 2-.*包内所有业务组件 3-.*所有方法 (..)所有方法
    @Pointcut("execution(* com.garen.community.service.*.*(..))")
    public void pointcut(){
    
    }
    //2.定义Advice通知(增强)
    //开头织入
    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }
    //后面织入 接日志
    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }
    
    //在返回值之后织入 接日志
    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("returning");
    }
    //在抛异常之后织入
    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("throwing");
    }
    
    //既在之前织入 也在join point退出之后织入 (最常用)
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("around-before it");
        Object obj = joinPoint.proceed();
        System.out.println("around-after");
        return obj;
    }
}
