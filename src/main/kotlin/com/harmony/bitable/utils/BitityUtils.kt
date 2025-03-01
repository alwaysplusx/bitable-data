package com.harmony.bitable.utils

import com.harmony.bitable.annotations.BitId
import com.harmony.bitable.annotations.Bitdex
import com.harmony.bitable.annotations.Bitfield
import org.springframework.beans.BeanUtils
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.data.mapping.model.Property
import org.springframework.data.util.TypeInformation
import org.springframework.util.ClassUtils
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * @author wuxin
 */
object BitityUtils {

    private fun containsAny(field: Field, vararg annotations: Class<out Annotation>): Boolean {
        return annotations.any { AnnotationUtils.findAnnotation(field, it) != null }
    }

    fun getBitityFields(type: Class<*>): List<Property> {
        val rawType = ClassUtils.getUserClass(type)
        val descriptors = BeanUtils.getPropertyDescriptors(rawType).associateBy { it.name }
        val typeInformation = TypeInformation.of(rawType)

        val result = mutableListOf<Property>()
        ReflectionUtils.doWithFields(type, {
            ReflectionUtils.makeAccessible(it)
            val descriptor = descriptors[it.name]
            val property = if (descriptor != null)
                Property.of(typeInformation, it, descriptor)
            else
                Property.of(typeInformation, it)
            result.add(property)
        }, BitityUtils::isBitfield)
        return result
    }

    private fun isBitfield(field: Field): Boolean {
        return !Modifier.isStatic(field.modifiers)
                && !Modifier.isFinal(field.modifiers)
                && containsAny(field, Bitfield::class.java, Bitdex::class.java, BitId::class.java)
    }

}
