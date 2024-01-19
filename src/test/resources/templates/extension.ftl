// auto codegen
package com.harmony.bitable.oapi.${group}

import com.harmony.bitable.oapi.PageCursor
import com.harmony.bitable.oapi.PageSlice
import com.harmony.bitable.oapi.SimplePageSlice
import com.harmony.bitable.oapi.ensurePage
import com.harmony.bitable.utils.PageUtils.scan

import com.lark.oapi.core.request.RequestOptions

<#list imports as t>
import ${t}
</#list>

<#list methods as m>
fun ${m.serviceName}.${m.name}Cursor(
    req: ${m.requestType.simpleName},
    options: RequestOptions = RequestOptions(),
): PageCursor<${m.responseItemType.simpleName}> {
    return scan(req.pageSize, req.pageToken) { pageable ->
        req.pageToken = pageable.pageToken
        req.pageSize = pageable.pageSize
        this.${m.name}(req, options).ensurePage { it.toPageSlice() }
    }
}

</#list>

<#list converters as c>
fun ${c.responseDataType.simpleName}.toPageSlice(): PageSlice<${c.responseItemType.simpleName}> {
    return SimplePageSlice(
        items = this.items.toList(),
        nextToken = this.pageToken,
        hasNextSlice = this.hasMore,
        total = this.total
    )
}
</#list>
