package com.harmony.bitable.utils

import com.harmony.bitable.core.NameFunction
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


object NameUtils {

    @JvmStatic
    fun read(func: NameFunction<*, *>): java.lang.invoke.SerializedLambda {
        try {
            val method: Method = func.javaClass.getDeclaredMethod("writeReplace")
            method.setAccessible(true)
            return method.invoke(func) as java.lang.invoke.SerializedLambda
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Cannot get SerializedLambda", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Cannot get SerializedLambda", e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Cannot get SerializedLambda", e)
        }
    }
}