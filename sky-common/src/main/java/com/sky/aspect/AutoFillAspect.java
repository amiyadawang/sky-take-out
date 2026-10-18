package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.weaver.AjAttribute;
import org.springframework.expression.Operation;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut() {

    }
    //前置通知
    @Before("autoFillPointcut()")
    public  void autoFill(JoinPoint joinPoint) {
        log.info("自动填充参数");
        //判断是update还是creat
        MethodSignature Signature =(MethodSignature) joinPoint.getSignature();
        AutoFill autoFill =Signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();
        //获取实体对象
        Object[] args=joinPoint.getArgs();
        if(args==null||args.length==0){
            return;
        }
        Object entity=args[0];//约定将对象放到第一位所以使用0
        LocalDateTime now =LocalDateTime.now();
        long current = BaseContext.getCurrentId();
        //统一赋值的数据
      if(operationType==OperationType.INSERT){
          try {

              Method setCreateTime = entity.getClass().getDeclaredMethod("AutoFillConstant.SET_CREATE_TIME", LocalDateTime.class);
              Method setCreateUser =entity.getClass().getDeclaredMethod("AutoFillConstant.SET_CREATE_USER", Long.class);
              Method setUpdateTime =entity.getClass().getDeclaredMethod("AutoFillConstant.SET_UPDATE_TIME", LocalDateTime.class);
              Method setUpdateUser =entity.getClass().getDeclaredMethod("AutoFillConstant.SET_UPDATE_USER", Long.class);




              setCreateTime.invoke(entity,now);
              setCreateUser.invoke(entity,current);
              setUpdateTime.invoke(entity,now);
              setUpdateUser.invoke(entity,current);
                     }catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
              e.printStackTrace();
          }
      }else if(operationType==OperationType.UPDATE){  try {


          Method setUpdateTime =entity.getClass().getDeclaredMethod("AutoFillConstant.SET_UPDATE_TIME", LocalDateTime.class);
          Method setUpdateUser =entity.getClass().getDeclaredMethod("AutoFillConstant.SET_UPDATE_USER", Long.class);





          setUpdateTime.invoke(entity,now);
          setUpdateUser.invoke(entity,current);
      }catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
          e.printStackTrace();
      }}
    }
}
