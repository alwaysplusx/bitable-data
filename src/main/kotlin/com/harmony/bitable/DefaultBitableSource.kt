package com.harmony.bitable

import com.harmony.bitable.annotations.BitId
import com.harmony.bitable.convert.BitvalConverter
import com.harmony.bitable.oapi.BitableApi
import com.harmony.bitable.oapi.getBitableType
import com.harmony.bitable.utils.BitityUtils
import org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation
import org.springframework.data.mapping.model.Property
import org.springframework.util.ClassUtils
import java.lang.reflect.AnnotatedElement

typealias BitableAnnotation = com.harmony.bitable.annotations.Bitable
typealias BitfieldAnnotation = com.harmony.bitable.annotations.Bitfield

/**
 * @author wuxin
 */
class DefaultBitableSource(private val appToken: String, private val bitableApi: BitableApi) : BitableSource {

    companion object {
        private val NULLABLE_BITFIELD_TYPES = setOf(
            BitfieldType.CREATED_AT, BitfieldType.CREATED_BY,
            BitfieldType.UPDATED_AT, BitfieldType.UPDATED_BY
        )
    }

    private val bitityCache = mutableMapOf<Class<*>, Bitity<*>>()

    override fun getAppToken() = appToken

    override fun <T> getBitity(domainType: Class<T>): Bitity<T> {
        val rawDomainType = ClassUtils.getUserClass(domainType)
        val bitableName = resolveBitableName(rawDomainType)
        return bitityCache.computeIfAbsent(rawDomainType) { doBuildBitity(bitableName, it) } as Bitity<T>
    }

    private fun <T> doBuildBitity(bitableName: String, type: Class<T>): Bitity<T> {
        val bitable = bitableApi.getBitable(appToken, bitableName)
        val fields = BitityUtils.getBitityFields(type).map {
            resolveAsBitityField(it, bitable)
        }
        if (fields.none { it.isRecordId }) {
            throw IllegalStateException("$type not have any field with @BitId")
        }
        return Bitity(
            name = bitableName,
            type = type,
            address = bitable.address,
            fields = fields
        )
    }

    private fun resolveAsBitityField(property: Property, bitable: Bitable): BitityField {
        val field = property.field.orElseThrow { throw IllegalStateException("field ${property.name} not found") }
        val bitfield = findAnnotation(field, BitfieldAnnotation::class.java)
        val isRecordId = findAnnotation(field, BitId::class.java) != null
        val fieldName = resolveFieldName(bitfield, property)
        val appField = bitable.getField(fieldName)
        val expectType = bitfield?.type ?: BitfieldType.AUTO
        if (appField == null && isAllowedAppFieldNotEmpty(isRecordId, expectType)) {
            throw IllegalStateException("$fieldName not found in table ${bitable.name}")
        }
        return BitityField(
            fieldName = fieldName,
            fieldType = appField?.getBitableType() ?: expectType,
            isRecordId = isRecordId,
            isReadonly = bitfield?.readonly ?: false,
            customizeConverter = bitfield?.converter ?: BitvalConverter::class,
            property = property,
            appField = appField
        )
    }

    private fun resolveBitableName(domainType: Class<*>): String {
        val bitable = findAnnotation(domainType, BitableAnnotation::class.java)
            ?: throw IllegalStateException("$domainType not have @Bitable annotation")
        return bitable.name.ifBlank { domainType.simpleName }
    }

    private fun resolveFieldName(annotation: BitfieldAnnotation?, property: Property): String {
        return (annotation?.name ?: "").ifBlank { property.name }
    }

    private fun <T : Annotation?> findAnnotation(element: AnnotatedElement, annotationType: Class<T>): T? {
        return findMergedAnnotation(element, annotationType)
    }

    private fun isAllowedAppFieldNotEmpty(isRecordId: Boolean, bitfieldType: BitfieldType): Boolean {
        return !(isRecordId || bitfieldType in NULLABLE_BITFIELD_TYPES)
    }

}