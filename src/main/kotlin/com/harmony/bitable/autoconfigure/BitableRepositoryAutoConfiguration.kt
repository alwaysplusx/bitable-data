package com.harmony.bitable.autoconfigure

import com.harmony.bitable.BitableSource
import com.harmony.bitable.BitableSourceImpl
import com.harmony.bitable.BititySource
import com.harmony.bitable.BititySourceImpl
import com.harmony.bitable.convert.MappingBitableConverter
import com.harmony.bitable.core.BitableTemplate
import com.harmony.bitable.mapping.BitableMappingContext
import com.harmony.bitable.mapping.BitableMappingContextImpl
import com.harmony.bitable.oapi.BitableApi
import com.harmony.bitable.oapi.BitableRecordApi
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.springframework.data.mapping.model.EntityInstantiators

@AutoConfigureAfter(BitableAutoConfiguration::class)
@ConditionalOnProperty(
    prefix = "bitable.repositories",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class BitableRepositoryAutoConfiguration(private val properties: BitableProperties) {

    @Bean
    @ConditionalOnMissingBean(BititySource::class)
    fun bititySource(): BititySource = BititySourceImpl()

    @Bean
    @ConditionalOnProperty(prefix = "bitable", name = ["app-token"])
    @ConditionalOnMissingBean(BitableSource::class)
    fun bitableSource(bitableApi: BitableApi): BitableSource = BitableSourceImpl(properties.appToken, bitableApi)

    @Bean
    @DependsOn(value = ["bititySource", "bitableSource"])
    @ConditionalOnMissingBean(BitableMappingContext::class)
    fun bitableMappingContext(bitableSource: BitableSource, bititySource: BititySource): BitableMappingContext {
        return BitableMappingContextImpl(bitableSource, bititySource)
    }

    @Bean
    @ConditionalOnBean(BitableRecordApi::class)
    @ConditionalOnMissingBean(BitableTemplate::class)
    fun bitableTemplate(
        bitableRecordApi: BitableRecordApi,
        bitableMappingContext: BitableMappingContext
    ): BitableTemplate {
        val bitableConverter = MappingBitableConverter(
            mappingContext = bitableMappingContext,
            entityInstantiators = EntityInstantiators()
        )
        return BitableTemplate(
            bitableRecordApi = bitableRecordApi,
            bitableMappingContext = bitableMappingContext,
            bitableConverter = bitableConverter
        )
    }

}
