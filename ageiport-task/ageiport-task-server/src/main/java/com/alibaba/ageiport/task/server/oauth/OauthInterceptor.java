package com.alibaba.ageiport.task.server.oauth;

import com.alibaba.ageiport.sdk.core.Request;
import com.alibaba.ageiport.task.server.config.TaskServerConfig;
import io.quarkus.logging.Log;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Oauth
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 1)
public class OauthInterceptor {

    private TaskServerConfig taskServerConfig;

    public OauthInterceptor(TaskServerConfig taskServerConfig) {
        this.taskServerConfig = taskServerConfig;
    }

    @AroundInvoke
    Object execute(InvocationContext context) {
        try {
            Object parameter = context.getParameters()[0];
            Request request = (Request) parameter;
            if (taskServerConfig.isEnableOauth2()) {
                //dosomething
            } else {
                request.setTenant(request.getApp());
            }
            return context.proceed();
        } catch (Exception exception) {
            Log.errorf(exception,
                    "method error from %s.%s\n",
                    context.getTarget().getClass().getSimpleName(),
                    context.getMethod().getName());
        }

        return null;
    }
}