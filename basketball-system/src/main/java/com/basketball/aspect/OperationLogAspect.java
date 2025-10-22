package com.basketball.aspect;

import com.alibaba.fastjson.JSON;
import com.basketball.annotation.OperationLog;
import com.basketball.security.JwtTokenProvider;
import com.basketball.service.IOperationLogService;
import com.basketball.utils.IpUtils;
import com.basketball.utils.UserAgentParser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 操作日志AOP切面
 */
@Aspect
@Component
public class OperationLogAspect {

    @Resource
    private IOperationLogService operationLogService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 定义切点：拦截所有带@OperationLog注解的方法
     */
    @Pointcut("@annotation(com.basketball.annotation.OperationLog)")
    public void operationLogPointcut() {
    }

    /**
     * 环绕通知：记录操作日志
     */
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String ip = IpUtils.getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String browser = UserAgentParser.getBrowser(userAgent);
        String os = UserAgentParser.getOperatingSystem(userAgent);

        // 获取用户信息
        Long userId = null;
        String username = null;
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                userId = jwtTokenProvider.getUserIdFromToken(token);
                // 这里可以从数据库查询用户名，暂时使用userId
                username = "user_" + userId;
            }
        } catch (Exception e) {
            // 忽略token解析异常
        }

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        String operation = operationLog.value();
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();

        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        String params = "";
        try {
            if (args != null && args.length > 0) {
                // 过滤掉HttpServletRequest等不需要序列化的参数
                Object[] filteredArgs = java.util.Arrays.stream(args)
                        .filter(arg -> !(arg instanceof HttpServletRequest)
                                && !(arg instanceof javax.servlet.http.HttpServletResponse)
                                && !(arg instanceof org.springframework.web.multipart.MultipartFile))
                        .toArray();
                params = JSON.toJSONString(filteredArgs);
                // 限制参数长度
                if (params.length() > 2000) {
                    params = params.substring(0, 2000) + "...";
                }
            }
        } catch (Exception e) {
            params = "参数序列化失败";
        }

        // 执行方法
        Object result = null;
        Integer status = 1;
        String errorMsg = null;
        String resultStr = "";

        try {
            result = joinPoint.proceed();
            // 获取返回结果
            try {
                resultStr = JSON.toJSONString(result);
                // 限制结果长度
                if (resultStr.length() > 2000) {
                    resultStr = resultStr.substring(0, 2000) + "...";
                }
            } catch (Exception e) {
                resultStr = "结果序列化失败";
            }
        } catch (Exception e) {
            status = 0;
            errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.length() > 500) {
                errorMsg = errorMsg.substring(0, 500) + "...";
            }
            throw e;
        } finally {
            // 计算执行时长
            long executeTime = System.currentTimeMillis() - startTime;

            // 异步记录日志（避免影响主业务）
            try {
                operationLogService.recordOperationLog(
                        userId,
                        username,
                        operation,
                        methodName,
                        params,
                        resultStr,
                        status,
                        ip,
                        null, // location暂时为空
                        browser,
                        os,
                        executeTime
                );
            } catch (Exception e) {
                // 记录日志失败不影响主业务
                e.printStackTrace();
            }
        }

        return result;
    }
}
