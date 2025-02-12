package com.harmony.bitable.codegen

import com.harmony.bitable.codegen.CodegenUtils.extensionTemplate
import com.harmony.bitable.codegen.CodegenUtils.loadPageMethods
import freemarker.template.Template
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.Writer

fun main() {

    val bitableResource = "com.lark.oapi.service.bitable.v1.resource"
    println("Will generate the resource: $bitableResource")

    val groupedMap = loadPageMethods(bitableResource).groupBy { it.group }
    val codegen = ExtensionCodegen(extensionTemplate())

    for (entry in groupedMap) {
        val group = entry.key
        val methods = entry.value.toList()
        codegen.gen(ExtensionModel(group, methods))
    }

}

fun getImports(methods: List<MethodModel>): List<TypeModel> {
    val result = mutableListOf<TypeModel>()

    val addIfAbsent = { type: TypeModel ->
        if (!type.name.startsWith("java.lang") && !result.contains(type)) {
            result.add(type)
        }
    }

    methods.forEach {
        addIfAbsent(it.responseDataType)
        addIfAbsent(it.responseItemType)
        addIfAbsent(it.requestType)
    }

    result.sortBy { it.name }
    return result
}

class ExtensionModel(
    val group: String,
    val methods: List<MethodModel>,
    val converters: List<MethodModel> = methods,
    val imports: List<TypeModel> = getImports(methods),
)


class ExtensionCodegen(private val template: Template) {

    private val baseDir: File = File("src/main/kotlin")

    private val basePackage = "com.harmony.bitable.oapi"

    fun gen(data: ExtensionModel) {
        getWriter(data.group).use {
            template.process(data, it)
        }
    }

    private fun getWriter(group: String): Writer {
        val path = "$basePackage.$group".replace(".", "/")
        val outputDir = File(baseDir.absoluteFile, path)

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val fileName = group.replaceFirstChar { it.uppercase() }
        val outputFile = File(outputDir, "${fileName}Extension.kt")
        println("will write to ${outputFile.absolutePath}")
        return OutputStreamWriter(FileOutputStream(outputFile))
    }

}
