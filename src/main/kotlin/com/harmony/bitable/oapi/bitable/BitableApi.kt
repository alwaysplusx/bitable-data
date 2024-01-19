package com.harmony.bitable.oapi.bitable

import com.harmony.bitable.Bitable
import com.harmony.bitable.BitableAddress
import com.harmony.bitable.oapi.PageCursor
import com.harmony.bitable.oapi.stream
import com.harmony.bitable.oapi.toList
import com.lark.oapi.Client
import com.lark.oapi.service.bitable.v1.model.AppTable
import com.lark.oapi.service.bitable.v1.model.AppTableFieldForList
import com.lark.oapi.service.bitable.v1.model.ListAppTableFieldReq
import com.lark.oapi.service.bitable.v1.model.ListAppTableReq

/**
 * 飞书多为表格的元数据相关接口操作
 */
class BitableApi(client: Client, private val pageSize: Int = 20) {

    private val appTable = client.bitable().appTable()
    private val appTableField = client.bitable().appTableField()

    /**
     * 从 [appToken](https://open.feishu.cn/document/server-docs/docs/bitable-v1/notification) 下获取与入参名称相同的多维表格(多维表格中表格名唯一)
     */
    fun getBitable(appToken: String, tableName: String): Bitable {
        val appTable = getAppTable(appToken, tableName)
        val address = BitableAddress(appToken, appTable.tableId)
        return doGetBitable(appTable, address)
    }

    fun getBitable(address: BitableAddress): Bitable {
        val appTable = getAppTable(address)
        return doGetBitable(appTable, address)
    }

    private fun doGetBitable(appTable: AppTable, address: BitableAddress): Bitable {
        val fields = getAppTableFields(address)
        return Bitable(name = appTable.name, address = address, fields = fields)
    }

    private fun getAppTableFields(address: BitableAddress): List<AppTableFieldForList> {
        return scanAppTableFields(address).toList()
    }

    private fun getAppTable(address: BitableAddress): AppTable {
        return scanAppTables(address.appToken)
            .stream()
            .filter { it.tableId == address.tableId }
            .findFirst()
            .orElseThrow { throw IllegalStateException("$address table not found") }
    }

    private fun getAppTable(appToken: String, tableName: String): AppTable {
        return scanAppTables(appToken)
            .stream()
            .filter { it.name == tableName }
            .findFirst()
            .orElseThrow { throw IllegalStateException("$tableName table not found") }
    }

    private fun scanAppTables(appToken: String): PageCursor<AppTable> {
        val request = ListAppTableReq.newBuilder()
            .pageSize(pageSize)
            .appToken(appToken)
            .build()
        return appTable.listCursor(request)
    }

    private fun scanAppTableFields(address: BitableAddress): PageCursor<AppTableFieldForList> {
        val request = ListAppTableFieldReq.newBuilder()
            .pageSize(pageSize)
            .appToken(address.appToken)
            .tableId(address.tableId)
            .build()
        return appTableField.listCursor(request)
    }

}
