package com.harmony.bitable.convert

import com.harmony.bitable.convert.bitval.*
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import org.slf4j.LoggerFactory
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.data.mapping.PersistentPropertyAccessor

/**
 * @author wuxin
 */
class DefaultBitfieldConverter(private val bitvalConverters: List<BitvalConverter> = defaultReaders) :
    BitfieldConverter {

    companion object {
        private val log = LoggerFactory.getLogger(DefaultBitfieldConverter::class.java)

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
        val bitvalConverter = bitvalConverters.stream()
            .filter { it.canRead(property) }
            .findFirst()
            .orElseThrow { IllegalArgumentException("No BitvalReader found for ${property.getBitfieldName()}") }
        val expectValueType = property.type
        val value = bitvalConverter.readAndConvert(property, record) ?: return null
        if (expectValueType.isInstance(value)) {
            throw IllegalArgumentException("Expected value type is ${expectValueType.name}, but got ${value.javaClass.name}")
        }
        return value
    }

    override fun readAndConvertPropertyValueFromAccessor(
        property: BitablePersistentProperty,
        accessor: PersistentPropertyAccessor<Any>
    ): Any? {
        val propertyValue = accessor.getProperty(property) ?: return null
        val resultValue =
            DefaultConversionService.getSharedInstance().convert(propertyValue, property.getBitfieldType().type)
        log.debug(
            "Get and convert property value of {}({})\ntype: {} -> {}\nvalue: {} -> {}",
            property.getBitfieldName(),
            property.name,
            property.type.name,
            property.getBitfieldType().type.name,
            propertyValue,
            resultValue
        )
        return resultValue
    }

}