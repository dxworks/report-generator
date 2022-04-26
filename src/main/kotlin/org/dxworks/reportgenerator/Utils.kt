package org.dxworks.reportgenerator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

val versionCommandArgs = setOf("-v", "version", "--version", "-version", "-V")

val yamlMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

fun writeDefaultConfigFile(resourcePath: String, targetFile: File) =
    object {}::class.java.classLoader.getResourceAsStream(resourcePath)
        ?.let { targetFile.writeBytes(it.readAllBytes()) }

