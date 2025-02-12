package com.harmony.bitable.codegen

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.util.ClassUtils
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

/**
 * @author wuxin
 */
object CodegenUtils {

    private val configuration = Configuration(Configuration.VERSION_2_3_31)

    private val PAGE_MUST_FIELDS = setOf("pageToken")

    private val PAGE_ALLOW_FIELDS = PAGE_MUST_FIELDS + "pageSize" + "total" + "hasMore"

    init {
        configuration.templateLoader = ClassTemplateLoader(Thread.currentThread().contextClassLoader, "templates")
    }

    fun extensionTemplate(): Template {
        return configuration.getTemplate("extension.ftl")
    }

    fun loadPageMethods(path: String): List<MethodModel> {
        val location = "classpath*:${path.replace(".", "/")}/*.class"
        val serviceTypes = PathMatchingResourcePatternResolver().getResources(location).map { forType(it) }

        val result = mutableListOf<MethodModel>()
        for (serviceType in serviceTypes) {
            val pageMethods = serviceType.type.methods.filter { isPageMethod(it) }
            if (pageMethods.isEmpty()) {
                continue
            }

            result.addAll(pageMethods.map { buildMethodModel(serviceType, it) })
        }

        result.sortWith(
            Comparator.comparing(MethodModel::group)
                .thenComparing(MethodModel::serviceName)
                .thenComparing(MethodModel::name)
        )
        return result
    }

    private fun buildMethodModel(serviceType: TypeModel, method: Method): MethodModel {
        val responseDataType = responseDataType(method.returnType)!!
        return MethodModel(
            serviceType = serviceType,
            group = getServiceGroup(serviceType),
            serviceName = getServiceName(serviceType),
            method = method,
            requestType = TypeModel(method.parameters[0].type),
            responseDataType = responseDataType,
            responseItemType = getResponseItemModel(responseDataType)
        )
    }

    private fun getServiceGroup(type: TypeModel): String {
        return "com\\.lark\\.oapi\\.service\\.(\\w*)".toRegex().find(type.name)!!.groupValues[1]
    }

    private fun getServiceName(type: TypeModel): String {
        return type.name
    }

    private fun isPageMethod(method: Method): Boolean {
        return method.parameters.size == 1 && isPageResult(method.returnType)
    }

    private fun isPageResult(resultTye: Class<*>): Boolean {
        val dataType = responseDataType(resultTye) ?: return false
        if (dataType.itemName == null) {
            return false
        }
        val allowFields = PAGE_ALLOW_FIELDS + dataType.itemName
        return dataType.fieldNames.size <= allowFields.size
                && dataType.fieldNames.all { allowFields.contains(it) }
                && PAGE_MUST_FIELDS.all { dataType.fieldNames.contains(it) }
    }

    private fun responseDataType(resultTye: Class<*>): ResponseDataModel? {
        if (resultTye.genericSuperclass !is ParameterizedType) {
            return null
        }
        val genericType = (resultTye.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        if (genericType !is Class<*>) {
            return null
        }
        return ResponseDataModel(type = genericType)
    }

    private fun getResponseItemModel(dataType: ResponseDataModel): TypeModel {
        val itemType: Class<*> = dataType.type.declaredFields.first { it.name == dataType.itemName }.type
        return TypeModel(itemType.componentType)
    }

    private fun forType(res: Resource): TypeModel {
        val name = res.url.toString()
            .substringAfter("!/")
            .replace("[/$]".toRegex(), ".")
            .removeSuffix(".class")
        return TypeModel(ClassUtils.forName(name, null))
    }

}

open class TypeModel(
    val type: Class<*>,
    val name: String = type.name.replace("$", "."),
    val simpleName: String = type.simpleName,
) {

    override fun toString() = name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TypeModel) return false

        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

}


class MethodModel(
    val serviceType: TypeModel,
    val group: String,
    val serviceName: String,
    val method: Method,
    val name: String = method.name,
    val requestType: TypeModel,
    val responseDataType: ResponseDataModel,
    val responseItemType: TypeModel,
) {

    override fun toString(): String {
        return "fun ${serviceName}.${name}(${requestType.simpleName}): BaseResponse<${responseDataType.simpleName}>"
    }

}

class ResponseDataModel(
    type: Class<*>,
    val fieldNames: List<String> = type.declaredFields.map { it.name },
    val hasTotal: Boolean = type.declaredFields.any { it.name == "total" },
    val itemName: String? = type.declaredFields.firstOrNull { it.type.isArray }?.name,
) : TypeModel(type) {

    fun hasField(name: String): Boolean = fieldNames.contains(name)

}
