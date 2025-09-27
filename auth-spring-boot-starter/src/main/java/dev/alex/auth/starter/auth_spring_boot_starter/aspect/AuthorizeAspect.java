package dev.alex.auth.starter.auth_spring_boot_starter.aspect;

import dev.alex.auth.starter.auth_spring_boot_starter.annotation.Authorize;
import dev.alex.auth.starter.auth_spring_boot_starter.exception.NoRightsException;
import dev.alex.auth.starter.auth_spring_boot_starter.model.UserContext;
import dev.alex.auth.starter.auth_spring_boot_starter.util.UserContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class AuthorizeAspect {

    @Around("@annotation(dev.alex.auth.starter.auth_spring_boot_starter.annotation.Authorize)")
    public Object validateAuthorizeMethod(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        Authorize annotation = method.getAnnotation(Authorize.class);

        String[] methodRoles = annotation.value();

        UserContext userContext = UserContextHolder.getContext();

        boolean auth = false;
        for (String role : methodRoles) {
            if (role.equalsIgnoreCase(userContext.getRole())) {
                auth = true;
                break;
            }
        }

        if (!auth) {
            throw new NoRightsException("Not enough rights!");
        }

        try {
            return point.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            UserContextHolder.remove();
        }
    }
}
