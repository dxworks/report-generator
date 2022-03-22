const {generateReport} = require('./lib')
const {Command} = require('commander')

exports.tablesCommand = new Command()
    .name('report-generator')
    .description('A command line utility to create a report from a pptx template')
    .allowUnknownOption()
    .action(generateReport)

