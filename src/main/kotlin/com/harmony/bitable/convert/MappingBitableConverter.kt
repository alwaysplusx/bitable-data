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
    private val bitfieldValueResolver: BitfieldValueResolver = DefaultBitfieldValueResolver()
) : BitableConverter {

    companion object {

        private val log = LoggerFactory.getLogger(MappingBitableConverter::class.java)

        private val NoOpParameterValueProvider = object : ParameterValueProvider<BitablePersistentProperty> {

            override fun <T : Any?> getParameterValue(parameter: Parameter<T, BitablePersistentProperty>) = null

        }

    }

    override fun <R : Any> read(type: Class<R>, source: AppTableRecord): R {
        val persistentEntity = mappingContext.getRequiredPersistentEntity(type)
        val instanceAccessor = createInstanceForAccessor(persistentEntity)

        val recordIdProperty = persistentEntity.getRecordIdProperty()
        if (recordIdProperty != null) {
            instanceAccessor.setProperty(recordIdProperty, source.recordId)
        }

        persistentEntity.forEach {
            val fieldValue = bitfieldValueResolver.readValue(it, source)
            instanceAccessor.setProperty(it, fieldValue)
        }
        return instanceAccessor.bean as R
    }

    override fun write(source: Any, sink: AppTableRecord) {
        val persistentEntity = mappingContext.getRequiredPersistentEntity(source.javaClass)
        val instanceAccessor = persistentEntity.getPropertyAccessor(source)

        val recordIdProperty = persistentEntity.getRecordIdProperty()
        if (recordIdProperty != null) {
            sink.recordId = instanceAccessor.getProperty(recordIdProperty)?.toString()
        }

        sink.fields = mutableMapOf()

        for (property in persistentEntity) {

            val fieldValue = getPropertyValue(property, instanceAccessor)

            if (property.isRecordIdProperty()) {
                sink.recordId = fieldValue?.toString()
            } else {
                sink.fields[property.getBitfieldName()] = fieldValue
            }

        }

    }

    override fun getMappingContext() = mappingContext

    override fun getConversionService(): ConversionService = DefaultConversionService.getSharedInstance()

    private fun getPropertyValue(
        property: BitablePersistentProperty,
        instanceAccessor: PersistentPropertyAccessor<Any>,
    ): Any? {
        val propertyValue = instanceAccessor.getProperty(property) ?: return null
        val resultValue = conversionService.convert(propertyValue, property.getBitfieldType().type)
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

    private fun <R> createInstanceForAccessor(persistentEntity: BitablePersistentEntity<R>): PersistentPropertyAccessor<R> {
        val instantiator = entityInstantiators.getInstantiatorFor(persistentEntity)
        val instance = instantiator.createInstance(persistentEntity, NoOpParameterValueProvider)
        return persistentEntity.getPropertyAccessor(instance)
    }

}
