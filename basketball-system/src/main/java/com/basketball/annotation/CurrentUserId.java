package com.basketball.annotation;

import java.lang.annotation.*;

/**
 * 当前用户ID注解
 * 标记在 Controller 方法参数上，自动注入当前登录用户ID
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUserId {
    
    /**
     * 是否必须（如果为true且用户未登录，则抛出异常）
     */
    boolean required() default true;
}

