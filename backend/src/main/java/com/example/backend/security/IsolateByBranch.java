package com.example.backend.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark service methods that should be isolated by branch.
 * For non-SuperAdmins, this will automatically inject the user's branchId 
 * or validate that the provided branchId matches the user's branch.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsolateByBranch {
    /**
     * The name of the parameter that represents the branchId.
     * If empty, the aspect will try to find a parameter named "branchId".
     */
    String value() default "branchId";
}
