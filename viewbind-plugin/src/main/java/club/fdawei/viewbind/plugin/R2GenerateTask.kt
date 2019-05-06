package club.fdawei.viewbind.plugin

import com.squareup.javapoet.JavaFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Created by david on 2019/04/30.
 */
open class R2GenerateTask : DefaultTask() {

    @get:Input
    var rFile: File? = null

    @get:Input
    var packageName: String? = null

    @get:OutputDirectory
    var outputDir: File? = null

    @TaskAction
    fun doAction() {
        val r2Builder = R2Builder()
        rFile?.forEachLine {
            processLine(it, r2Builder)
        }
        val javaFile = JavaFile.builder(packageName, r2Builder.build())
            .addFileComment("Generated by viewbind-plugin. Do not modify!")
            .build()
        javaFile.writeTo(outputDir)
    }

    private fun processLine(line: String, r2Builder: R2Builder) {
        val strBlocks = line.split(' ')
        if (strBlocks.size < 4 || strBlocks[0] != "int") {
            return
        }
        if (strBlocks[1] != "id") {
            return
        }
        r2Builder.add(strBlocks[1], strBlocks[2], strBlocks[3])
    }
}