package com.harmony.bitable.autoconfigure

import com.harmony.bitable.oapi.BitableApi
import com.harmony.bitable.oapi.BitableRecordApi
import com.harmony.bitable.oapi.bitable.BitableApiImpl
import com.harmony.bitable.oapi.bitable.BitableRecordApiImpl
import com.lark.oapi.service.bitable.v1.resource.AppTable
import com.lark.oapi.service.bitable.v1.resource.AppTableField
import com.lark.oapi.service.bitable.v1.resource.AppTableRecord
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@AutoConfigureAfter(LarkAutoConfiguration::class)
@ConditionalOnBean(value = [AppTable::class, AppTableField::class, AppTableRecord::class])
@EnableConfigurationProperties(BitableProperties::class)
class BitableAutoConfiguration(private val properties: BitableProperties) {

    @Bean
    @ConditionalOnMissingBean(BitableApi::class)
    fun bitableApi(appTable: AppTable, appTableField: AppTableField): BitableApi =
        BitableApiImpl(appTable, appTableField, properties.defaultPageSize)

    @Bean
    @ConditionalOnMissingBean(BitableRecordApi::class)
    fun bitableRecordApi(appTableRecord: AppTableRecord): BitableRecordApi = BitableRecordApiImpl(appTableRecord)

}

