package com.harmony.bitable.convert

import com.harmony.bitable.convert.bitval.*
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import org.springframework.util.ClassUtils
import kotlin.reflect.KClass

/**
 * @author wuxin
 */
class DefaultBitfieldConverter(private val bitvalConverters: List<BitvalConverter> = defaultReaders) :
    BitfieldConverter {

    companion object {
        private val defaultReaders = listOf(
            RecordIdConverter(),
            AttachmentConverter(),
            CheckboxConverter(),
            CreatedAtConverter(),
            CreatedByConverter(),
            DateTimeConverter(),
            FormulaConverter(),
            GroupConverter(),
            LocationConverter(),
            MultiSelectConverter(),
            NumberConverter(),
            PersonConverter(),
            PhoneNumberConverter(),
            SingleSelectConverter(),
            TextConverter(),
            UpdatedAtConverter(),
            UpdatedByConverter(),
            UrlConverter(),
        )
    }

    override fun readAndConvertFieldValueFromRecord(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val bitvalConverter = findBitvalConverter(property)
        return bitvalConverter.readAndConvert(property, record)
    }

    override fun convertAndWritePropertyValueToRecord(
        propertyValue: Any?,
        property: BitablePersistentProperty,
        record: AppTableRecord
    ) {
        val bitvalConverter = findBitvalConverter(property)
        bitvalConverter.convertAndWrite(propertyValue, property, record)
    }

    private fun findBitvalConverter(property: BitablePersistentProperty): BitvalConverter {
        val customizeConverterType = property.getCustomizeConverterType()
        if (customizeConverterType != BitvalConverter::class) {
            return createBitvalConverter(customizeConverterType)
        }
        return bitvalConverters.stream()
            .filter { it.canHandle(property) }
            .findFirst()
            .orElseThrow { IllegalArgumentException("No BitvalReader found for ${property.getBitfieldName()}") }
    }

    private fun createBitvalConverter(convertType: KClass<BitvalConverter>): BitvalConverter {
        val constructor = ClassUtils.getConstructorIfAvailable(convertType.java)
            ?: throw IllegalArgumentException("No default constructor for $convertType")
        return constructor.newInstance()
    }

}