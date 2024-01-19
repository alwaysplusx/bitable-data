package com.harmony.bitable.autoconfigure

import com.harmony.bitable.BitityService
import com.harmony.bitable.DefaultBitityService
import com.harmony.bitable.convert.BitableConverter
import com.harmony.bitable.convert.BitableConverters
import com.harmony.bitable.convert.BitfieldConverter
import com.harmony.bitable.convert.MappingBitableConverter
import com.harmony.bitable.convert.MappingBitableConverter.Companion.DEFAULT_CONVERSION_SERVICE
import com.harmony.bitable.core.BitableSource
import com.harmony.bitable.core.BitableSourceImpl
import com.harmony.bitable.core.BitableTemplate
import com.harmony.bitable.mapping.BitableMappingContext
import com.harmony.bitable.mapping.BitableMappingContextImpl
import com.harmony.bitable.oapi.bitable.BitableApi
import com.harmony.bitable.oapi.bitable.BitableRecordApi
import com.lark.oapi.Client
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.core.convert.ConversionService
import org.springframework.data.mapping.model.EntityInstantiators

@EnableConfigurationProperties(BitableProperties::class)
class BitableAutoConfiguration(private val bitableProperties: BitableProperties) {

    @Bean
    @ConditionalOnMissingBean(BitityService::class)
    fun bitityService(): BitityService = DefaultBitityService()

    @Bean
    @ConditionalOnProperty(prefix = "bitable", name = ["app-token"])
    @ConditionalOnMissingBean(BitableSource::class)
    fun bitableSource(client: Client): BitableSource {
        return BitableSourceImpl(bitableProperties.appToken, BitableApi(client))
    }

    @Bean
    @ConditionalOnBean(BitityService::class, BitableSource::class)
    @ConditionalOnMissingBean(BitableMappingContext::class)
    private fun bitableMappingContext(
        bitityService: BitityService,
        bitableSource: BitableSource,
    ): BitableMappingContext {
        return BitableMappingContextImpl(bitableSource, bitityService)
    }

    @Bean
    @ConditionalOnBean(BitableMappingContext::class)
    @ConditionalOnMissingBean(BitableConverter::class)
    fun bitableConverter(
        bitableMappingContext: BitableMappingContext,
        bitfieldConverter: ObjectProvider<BitfieldConverter>,
        @Qualifier(DEFAULT_CONVERSION_SERVICE) @Autowired(required = false) defaultConversionService: ConversionService?,
    ): BitableConverter {

        val bitableConverter = MappingBitableConverter(
            mappingContext = bitableMappingContext,
            entityInstantiators = EntityInstantiators(),
            defaultConversionService = defaultConversionService ?: BitableConverters.defaultConversionService()
        )

        bitfieldConverter.forEach { bitableConverter.addConverter(it) }

        return bitableConverter
    }

    @Bean
    @ConditionalOnBean(Client::class)
    @ConditionalOnMissingBean(BitableTemplate::class)
    fun bitableTemplate(
        client: Client,
        bitableMappingContext: BitableMappingContext,
        bitableConverter: BitableConverter,
    ): BitableTemplate {
        return BitableTemplate(
            recordApi = BitableRecordApi(client),
            mappingContext = bitableMappingContext,
            converter = bitableConverter
        )
    }

}
