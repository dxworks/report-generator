package org.dxworks.reportgenerator

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default

class ReportGenerator: CliktCommand(
    name= "report-generator",
    help = "A command line utility to create a report from a pptx template."
) {

    private val configFile by argument(
        "file",
        help = "The yaml configuration file"
    ).default("report-config.yml")
    override fun run() {
        println("Running on $configFile")
    }
}