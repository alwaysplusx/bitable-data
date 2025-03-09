package com.harmony.bitable.core

import com.harmony.bitable.convert.BitfieldValueMap
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReq
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReqBody
import com.lark.oapi.service.bitable.v1.model.UpdateAppTableRecordReq

typealias SearchRequest = SearchAppTableRecordReq

typealias SearchBody = SearchAppTableRecordReqBody

typealias SearchRequestBuilder = SearchAppTableRecordReq.Builder

typealias SearchBodyBuilder = SearchAppTableRecordReqBody.Builder

typealias UpdateRequest = UpdateAppTableRecordReq

typealias UpdateBody = AppTableRecord

typealias UpdateRequestBuilder = UpdateAppTableRecordReq.Builder

typealias UpdateBodyBuilder = AppTableRecord.Builder

fun <T> AppTableRecord.bitfieldValueMap(domainType: Class<T>): BitfieldValueMap {
    return BitfieldValueMap(domainType, fields)
}