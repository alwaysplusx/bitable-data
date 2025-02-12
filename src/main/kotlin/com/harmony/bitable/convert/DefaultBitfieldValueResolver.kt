package com.harmony.bitable.convert

import com.harmony.bitable.convert.bitval.BitvalReader
import com.harmony.bitable.convert.bitval.impl.*
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
class DefaultBitfieldValueResolver(private val bitvalReaders: List<BitvalReader> = defaultReaders) :
    BitfieldValueResolver {

    companion object {
        private val defaultReaders = listOf(
            RecordIdBitvalReader(),
            AttachmentBitvalReader(),
            CheckboxBitvalReader(),
            CreatedAtBitvalReader(),
            CreatedByBitvalReader(),
            DateTimeBitvalReader(),
            FormulaBitvalReader(),
            GroupBitvalReader(),
            LocationBitvalReader(),
            MultiSelectBitvalReader(),
            NumberBitvalReader(),
            PersonBitvalReader(),
            PhoneNumberBitvalReader(),
            SingleSelectBitvalReader(),
            TextBitvalReader(),
            UpdatedAtBitvalReader(),
            UpdatedByBitvalReader(),
            UrlBitvalReader(),
        )
    }

    override fun readValue(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        return filterBitvalReader(property).read(property, record)
    }

    private fun filterBitvalReader(property: BitablePersistentProperty): BitvalReader {
        return bitvalReaders.firstOrNull { it.canRead(property) }
            ?: throw IllegalArgumentException("No BitvalReader found for property: $property")
    }

}