package com.harmony.bitable.oapi.ratelimiter

import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import org.springframework.cglib.proxy.MethodInterceptor
import org.springframework.cglib.proxy.MethodProxy
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * @author wuxin
 */
class RateLimiterMethodInterceptor(
    private val target: Any,
    private val rateLimiterRegistry: RateLimiterRegistry
) : MethodInterceptor {

    override fun intercept(obj: Any, method: Method, args: Array<out Any>?, proxy: MethodProxy?): Any? {
        val limiter = findRateLimiter(method) ?: return invokeMethod(target, method, args ?: emptyArray())
        return limiter.executeCheckedSupplier {
            invokeMethod(target, method, args ?: emptyArray())
        }
    }

    private fun invokeMethod(target: Any, method: Method, args: Array<out Any>): Any? {
        try {
            return method.invoke(target, *args)
        } catch (e: InvocationTargetException) {
            throw e.targetException ?: e
        } catch (e: Exception) {
            throw e
        }
    }

    private fun findRateLimiter(method: Method): RateLimiter? {
        val name = method.declaringClass.simpleName + "-" + method.name
        return rateLimiterRegistry.allRateLimiters.firstOrNull { it.name == name }
    }

}