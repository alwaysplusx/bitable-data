package com.harmony.bitable.oapi.ratelimiter

import io.github.resilience4j.core.lang.Nullable
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import org.springframework.core.annotation.AnnotationUtils
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * @author wuxin
 */
class RateLimiterInvocationHandler(
    private val target: Any,
    private val rateLimiterRegistry: RateLimiterRegistry
) : InvocationHandler {

    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any? {
        return doInvoke(proxy, method, args ?: emptyArray())
    }

    private fun doInvoke(proxy: Any?, method: Method, args: Array<out Any>): Any? {
        val annotation = getRateLimiterAnnotation(method) ?: return method.invoke(target, *args)
        val hasRateLimiter = rateLimiterRegistry.allRateLimiters.any { it.name == annotation.name }
        if (!hasRateLimiter) {
            return invokeMethod(method, args)
        }
        val rateLimiter = rateLimiterRegistry.rateLimiter(annotation.name)
        return rateLimiter.executeCheckedSupplier {
            invokeMethod(method, args)
        }
    }

    private fun invokeMethod(method: Method, args: Array<out Any>): Any? {
        try {
            return method.invoke(target, *args)
        } catch (e: InvocationTargetException) {
            throw e.targetException ?: e
        } catch (e: Exception) {
            throw e
        }
    }

    @Nullable
    private fun getRateLimiterAnnotation(method: Method): RateLimiter? {
        return AnnotationUtils.getAnnotation(method, RateLimiter::class.java)
    }

}