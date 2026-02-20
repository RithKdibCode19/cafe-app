package com.example.backend.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class BranchSecurityAspect {

    @Around("@annotation(isolateByBranch)")
    public Object enforceBranchIsolation(ProceedingJoinPoint joinPoint, IsolateByBranch isolateByBranch) throws Throwable {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetailsImpl)) {
            return joinPoint.proceed();
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        // If SuperAdmin, allow everything
        boolean isSuperAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> "SYS_ALL".equals(a.getAuthority()));
        
        if (isSuperAdmin) {
            return joinPoint.proceed();
        }

        Long userBranchId = userDetails.getBranchId();
        if (userBranchId == null) {
            throw new AccessDeniedException("User is not assigned to any branch");
        }

        String targetParamName = isolateByBranch.value();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        int index = -1;
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(targetParamName)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            // If the parameter name doesn't match, we can't enforce isolation this way
            // This might happen if the annotation is used incorrectly
            return joinPoint.proceed();
        }

        Object arg = args[index];

        if (arg == null) {
            // Auto-inject the user's branchId (only if it's a Long parameter)
            if (signature.getParameterTypes()[index].equals(Long.class)) {
                args[index] = userBranchId;
            }
        } else if (arg instanceof Long) {
            // Validate that provided branchId matches user's branchId
            if (!arg.equals(userBranchId)) {
                throw new AccessDeniedException("Unauthorized access to branch: " + arg);
            }
        } else {
            // Check for getBranchId method via reflection
            try {
                java.lang.reflect.Method getBranchIdMethod = arg.getClass().getMethod("getBranchId");
                Object branchId = getBranchIdMethod.invoke(arg);
                
                if (branchId == null) {
                    // Try to set it if there's a setBranchId method
                    try {
                        java.lang.reflect.Method setBranchIdMethod = arg.getClass().getMethod("setBranchId", Long.class);
                        setBranchIdMethod.invoke(arg, userBranchId);
                    } catch (Exception e) {
                        // ignore if no setter
                    }
                } else if (branchId instanceof Long) {
                    if (!branchId.equals(userBranchId)) {
                        throw new AccessDeniedException("Unauthorized access to branch: " + branchId);
                    }
                }
            } catch (NoSuchMethodException e) {
                // If no getBranchId, just proceed
            } catch (Exception e) {
                // Other reflection errors
            }
        }

        return joinPoint.proceed(args);
    }
}
