package com.basketball.resolver;

import com.basketball.annotation.CurrentUserId;
import com.basketball.common.exception.BusinessException;
import com.basketball.security.UserContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 当前用户ID参数解析器
 * 自动将 ThreadLocal 中的用户ID注入到标记了 @CurrentUserId 注解的参数
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 支持标记了 @CurrentUserId 注解的 Long 类型参数
        return parameter.hasParameterAnnotation(CurrentUserId.class) 
                && Long.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                   NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Long userId = UserContext.getUserId();
        
        CurrentUserId annotation = parameter.getParameterAnnotation(CurrentUserId.class);
        
        // 如果标记为必须但用户未登录，抛出异常
        if (annotation != null && annotation.required() && userId == null) {
            throw new BusinessException("用户未登录或登录已过期，请重新登录");
        }
        
        return userId;
    }
}

