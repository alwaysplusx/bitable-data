package com.harmony.bitable

import com.harmony.bitable.annotations.BitId
import com.harmony.bitable.oapi.BitableApi
import com.harmony.bitable.utils.BitityUtils
import com.harmony.bitable.utils.BitityUtils.getBitfieldType
import org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation
import org.springframework.data.mapping.model.Property
import org.springframework.util.ClassUtils

typealias BitableAnnotation = com.harmony.bitable.annotations.Bitable
typealias BitfieldAnnotation = com.harmony.bitable.annotations.Bitfield

/**
 * @author wuxin
 */
class DefaultBitableSource(private val appToken: String, private val bitableApi: BitableApi) : BitableSource {

    private val bitityCache = mutableMapOf<Class<*>, Bitity<*>>()

    override fun getBitable(name: String) = bitableApi.getBitable(appToken, name)

    override fun <T> getBitity(type: Class<T>): Bitity<T> {
        val rowType = ClassUtils.getUserClass(type)
        return bitityCache.computeIfAbsent(rowType) { doBuildBitity(it) } as Bitity<T>
    }

    private fun <T> doBuildBitity(type: Class<T>): Bitity<T> {
        val bitableAnnotation = findBitableAnnotation(type)
            ?: throw IllegalStateException("$type not have @Bitable annotation")
        val bitableName = bitableAnnotation.name.ifBlank { type.simpleName }
        val bitfields = BitityUtils.getBitityFields(type).map { parseBitityField(it) }
        if (bitfields.none { it.isRecordIdField }) {
            throw IllegalStateException("$type not have any field with @BitId")
        }
        return Bitity(name = bitableName, type = type, fields = bitfields)
    }

    private fun parseBitityField(property: Property): BitityField {
        if (property.field.isEmpty) {
            throw IllegalStateException("field ${property.name} not found")
        }
        val field = property.field.get()
        val bitfieldAnnotation = findMergedAnnotation(field, BitfieldAnnotation::class.java)
        val bitIdAnnotation = findMergedAnnotation(field, BitId::class.java)
        return BitityField(
            fieldId = null,
            fieldName = resolveFieldName(bitfieldAnnotation, property),
            fieldType = resolveFieldType(bitfieldAnnotation, property),
            property = property,
            isRecordIdField = bitIdAnnotation != null
        )
    }

    private fun resolveFieldType(annotation: BitfieldAnnotation?, property: Property): BitfieldType {
        if (annotation != null && annotation.type != BitfieldType.AUTO) {
            return annotation.type
        }
        return getBitfieldType(property.type)
            ?: throw IllegalStateException("${property.type.simpleName} not have default bitfield type")
    }

    private fun resolveFieldName(annotation: BitfieldAnnotation?, property: Property): String {
        return (annotation?.name ?: "").ifBlank { property.name }
    }

    private fun findBitableAnnotation(type: Class<*>): BitableAnnotation? {
        return findMergedAnnotation(ClassUtils.getUserClass(type), BitableAnnotation::class.java)
    }

}