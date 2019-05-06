package club.fdawei.viewbind.plugin

import com.android.build.gradle.*
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.res.GenerateLibraryRFileTask
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import groovy.util.XmlSlurper
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by david on 2019/04/29.
 */
class ViewBindPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.all {
            when(it) {
                is FeaturePlugin -> {
                    project.extensions.findByType(FeatureExtension::class.java)?.run {
                        configR2GenerateTask(project, featureVariants)
                        configR2GenerateTask(project, libraryVariants)
                    }
                }
                is LibraryPlugin -> {
                    project.extensions.findByType(LibraryExtension::class.java)?.run {
                        configR2GenerateTask(project, libraryVariants)
                    }
                }
                is AppPlugin -> {
                    project.extensions.findByType(AppExtension::class.java)?.run {
                        configR2GenerateTask(project, applicationVariants)
                    }
                }
            }
        }
    }

    private fun configR2GenerateTask(project: Project, variants: DomainObjectSet<out BaseVariant>) {
        variants.all { variant ->
            val outputDir = project.buildDir.resolve("generated/source/r2/${variant.dirName}")
            val rPackageName = getPackageName(variant)
            val hasConfig = AtomicBoolean(false)
            variant.outputs.all { output ->
                if (hasConfig.compareAndSet(false, true)) {
                    val processResourcesTask = output.processResourcesProvider.get()
                    val rConfigurableFiles = project.files(
                        when (processResourcesTask) {
                            is LinkApplicationAndroidResourcesTask -> processResourcesTask.textSymbolOutputFile
                            is GenerateLibraryRFileTask -> processResourcesTask.textSymbolOutputFile
                            else -> throw RuntimeException("Unknow processResourcesTask type")
                        }
                    ).builtBy(processResourcesTask)
                    project.tasks.create("generate${variant.name.capitalize()}R2", R2GenerateTask::class.java) {
                        it.rFile = rConfigurableFiles.singleFile
                        it.packageName = rPackageName
                        it.outputDir = outputDir
                        variant.registerJavaGeneratingTask(it, outputDir)
                    }
                }
            }
        }
    }

    private fun getPackageName(variant: BaseVariant): String {
        val xmlSlurper = XmlSlurper(false, false)
        val manifestList = variant.sourceSets.map { it.manifestFile }
        val result = xmlSlurper.parse(manifestList[0])
        return result.getProperty("@package").toString()
    }
}