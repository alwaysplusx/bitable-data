package com.harmony.bitable.oapi

class Pageable(val pageSize: Int = 20, val pageToken: String? = null) {

    fun next(pageToken: String) = Pageable(pageSize, pageToken)

}
