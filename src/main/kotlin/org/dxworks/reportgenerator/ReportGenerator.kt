package org.dxworks.reportgenerator

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import org.apache.poi.util.IOUtils
import org.apache.poi.xslf.usermodel.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class ReportGenerator: CliktCommand(
    name= "report-generator",
    help = "A command line utility to create a report from a pptx template."
) {

    private val configFile by argument(
        "file",
        help = "The yaml configuration file"
    ).default("report-config.yml")
    override fun run() {
        //println("Running on $configFile")

        val config: ReportConfig = yamlMapper.readValue(File(configFile))
        //println(config.slides.keys.elementAt(0))

        //create ppt from template
        val ppt = XMLSlideShow(FileInputStream(config.template))

        for (slide in config.slides){
            val slideBeingModified = ppt.slides[slide.key.toInt() - 1]

            //println(slideBeingModified.shapes::class.java.typeName)
            //ArrayList of XSLFShape

            for(placeholder in slide.value.keys){
                for(shape in slideBeingModified.shapes)
                    if(shape.shapeName.equals(placeholder)) {
                        //println(slide.value[placeholder])

                        if (shape is XSLFTextShape) {
                            shape.clearText()
                            shape.text = slide.value[placeholder]
                        } else if (shape is XSLFPictureShape) {
                            //search for picture in config.illustrationFolders
                            var picturePath = ""
                            for (folderPath in config.illustrationFolders){
                                val fullPath = folderPath + "/" + slide.value[placeholder]
                                val file = File(fullPath)
                                if(file.isFile) {
                                    picturePath = fullPath
                                    break
                                }
                            }

                            val pictureData: ByteArray =
                                IOUtils.toByteArray(FileInputStream(picturePath))
                            shape.pictureData.setData(pictureData)
                        }

                        break
                    }
            }

        }

        //save ppt
        val out = FileOutputStream("result-powerpoint.pptx")
        ppt.write(out)
        out.close()
    }
}