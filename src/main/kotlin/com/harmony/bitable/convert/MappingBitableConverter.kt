package com.harmony.bitable.convert

import com.harmony.bitable.mapping.BitableMappingContext
import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.data.mapping.Parameter
import org.springframework.data.mapping.PersistentProperty
import org.springframework.data.mapping.PersistentPropertyAccessor
import org.springframework.data.mapping.model.EntityInstantiators
import org.springframework.data.mapping.model.ParameterValueProvider

class MappingBitableConverter(
    private val mappingContext: BitableMappingContext,
    private val entityInstantiators: EntityInstantiators = EntityInstantiators(),
    private val bitfieldConverter: BitfieldConverter = DefaultBitfieldConverter()
) : BitableConverter {

    companion object {

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
        val persistentEntity: BitablePersistentEntity<*>
        val accessor: PersistentPropertyAccessor<out Any>

        if (source is BitfieldValueMap) {
            persistentEntity = mappingContext.getRequiredPersistentEntity(source.domainType)
            accessor = BitfieldValueMapPersistentPropertyAccessor(source)
        } else {
            persistentEntity = mappingContext.getRequiredPersistentEntity(source.javaClass)
            accessor = persistentEntity.getPropertyAccessor(source)
        }

        sink.fields = mutableMapOf()
        persistentEntity.filter { !it.isReadonly() }.forEach {
            val propertyValue = accessor.getProperty(it)
            bitfieldConverter.convertAndWritePropertyValueToRecord(propertyValue, it, sink)
        }
    }

    override fun getMappingContext() = mappingContext

    override fun getConversionService(): ConversionService = DefaultConversionService.getSharedInstance()

    private fun <R> createPersistentPropertyAccessor(persistentEntity: BitablePersistentEntity<R>): PersistentPropertyAccessor<R> {
        val instantiator = entityInstantiators.getInstantiatorFor(persistentEntity)
        val instance = instantiator.createInstance(persistentEntity, NoOpParameterValueProvider)
        return persistentEntity.getPropertyAccessor(instance)
    }

    private class BitfieldValueMapPersistentPropertyAccessor(private val bitfieldValueMap: BitfieldValueMap) :
        PersistentPropertyAccessor<BitfieldValueMap> {

        override fun setProperty(property: PersistentProperty<*>, value: Any?) {
            throw UnsupportedOperationException("Not supported")
        }

        override fun getProperty(property: PersistentProperty<*>): Any? {
            if (property !is BitablePersistentProperty) {
                throw IllegalArgumentException("property must be BitablePersistentProperty")
            }
            return bitfieldValueMap.valueMap[property.getBitfieldName()]
        }

        override fun getBean() = bitfieldValueMap

    }

}
