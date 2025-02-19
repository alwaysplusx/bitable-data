package com.harmony.bitable.convert

import com.harmony.bitable.mapping.BitableMappingContext
import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import org.slf4j.LoggerFactory
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.data.mapping.Parameter
import org.springframework.data.mapping.PersistentPropertyAccessor
import org.springframework.data.mapping.model.EntityInstantiators
import org.springframework.data.mapping.model.ParameterValueProvider

class MappingBitableConverter(
    private val mappingContext: BitableMappingContext,
    private val entityInstantiators: EntityInstantiators = EntityInstantiators(),
    private val bitfieldConverter: BitfieldConverter = DefaultBitfieldConverter()
) : BitableConverter {

    companion object {

        private val log = LoggerFactory.getLogger(MappingBitableConverter::class.java)

        private val NoOpParameterValueProvider = object : ParameterValueProvider<BitablePersistentProperty> {

            override fun <T : Any?> getParameterValue(parameter: Parameter<T, BitablePersistentProperty>) = null

        }

    }

    override fun <R : Any> read(type: Class<R>, source: AppTableRecord): R {
        val persistentEntity = mappingContext.getRequiredPersistentEntity(type)
        val accessor = createPersistentPropertyAccessor(persistentEntity)

        persistentEntity.forEach {
            val fieldValue = bitfieldConverter.readAndConvertFieldValueFromRecord(it, source)
            accessor.setProperty(it, fieldValue)
        }
        return accessor.bean as R
    }

    override fun write(source: Any, sink: AppTableRecord) {
        val persistentEntity = mappingContext.getRequiredPersistentEntity(source.javaClass)
        val accessor = persistentEntity.getPropertyAccessor(source)

        sink.fields = mutableMapOf()
        persistentEntity.forEach {
            val fieldValue = bitfieldConverter.readAndConvertPropertyValueFromAccessor(it, accessor)
            if (it.isRecordIdProperty()) {
                sink.recordId = fieldValue?.toString()
            } else {
                sink.fields[it.getBitfieldName()] = fieldValue
            }
        }
    }

    override fun getMappingContext() = mappingContext

    override fun getConversionService(): ConversionService = DefaultConversionService.getSharedInstance()

    private fun <R> createPersistentPropertyAccessor(persistentEntity: BitablePersistentEntity<R>): PersistentPropertyAccessor<R> {
        val instantiator = entityInstantiators.getInstantiatorFor(persistentEntity)
        val instance = instantiator.createInstance(persistentEntity, NoOpParameterValueProvider)
        return persistentEntity.getPropertyAccessor(instance)
    }

}
