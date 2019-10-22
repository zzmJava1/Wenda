package com.newcode.Aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class myAspect {
    @Pointcut("execution(* com.newcode.controller.*.*(..))")
    public void myAspect(){}

    @Around("myAspect()")
    public void around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("记录日志");
        proceedingJoinPoint.proceed();
        System.out.println("方法结束");

    }
}
