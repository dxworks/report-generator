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
import kotlin.system.exitProcess


class ReportGenerator: CliktCommand(
    name= "report-generator",
    help = "A command line utility to create a report from a pptx template."
) {

    private val configFile by argument(
        "file",
        help = "The yaml configuration file"
    ).default("template-file.yml")
    override fun run() {
        //println("Running on $configFile")

        val config: ReportConfig = yamlMapper.readValue(File(configFile))

        //create ppt from template
        val ppt: XMLSlideShow
        try{
            ppt = XMLSlideShow(FileInputStream(config.template))
        } catch (e: java.io.IOException) {
            println(e.message)
            exitProcess(1)
        }

        for (slide in config.slides){
            //check slide number boundaries
            val slideNumber = slide.key.toInt()
            if(slideNumber > ppt.slides.size || slideNumber <= 0){
                println("Wrong slide number in config file")
                exitProcess(1)
            }
            val slideBeingModified = ppt.slides[slideNumber - 1]

            //look for match between yaml placeholder and slide shapes to modify
            for(placeholder in slide.value.keys){
                var foundShape = false
                for(shape in slideBeingModified.shapes)
                    if(shape.shapeName.equals(placeholder)) {
                        foundShape = true

                        if (shape is XSLFTextShape) {
                            shape.clearText()
                            shape.setText(slide.value[placeholder])
                        } else if (shape is XSLFPictureShape) {
                            //search for picture in config.illustrationFolders given paths
                            var fullPath = ""
                            val picturePath = slide.value[placeholder]
                            for (folderPath in config.illustrationFolders){
                                val path = folderPath + "/" + picturePath
                                val file = File(path)
                                if(file.isFile) {
                                    fullPath = path
                                    break
                                }
                            }
                            if(fullPath == "")
                                println("Could not find picture $picturePath for slide $slideNumber")
                            else{
                                val pictureData: ByteArray = IOUtils.toByteArray(FileInputStream(fullPath))
                                shape.pictureData.setData(pictureData)
                            }
                        }
                        break
                    }
                if(!foundShape)
                    println("Could not find placeholder $placeholder in slide $slideNumber")
            }

        }

        //save ppt
        val out = FileOutputStream("result-powerpoint.pptx")
        ppt.write(out)
        out.close()
    }
}