const {JavaCaller} = require('java-caller')

async function generateReport(arguments) {
    const java = new JavaCaller({
        jar: 'report-generator.jar', // CLASSPATH referencing the package embedded jar files
        mainClass: 'org.dxworks.reportGenerator.MainKt',// Main class to call, must be available from CLASSPATH,
        rootPath: __dirname,
        minimumJavaVersion: 11,
        output: 'console'
    })

    let args = arguments
    if (!arguments) {
        args = [...process.argv]
        let index = args.indexOf('report-generator') //if it is called from dxw cli
        if (index === -1)
            index = 1
        args.splice(0, index + 1)
    }

    const {status} = await java.run(args, {cwd: process.cwd()})
    process.exitCode = status
}


module.exports = {generateReport}
