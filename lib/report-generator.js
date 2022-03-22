#! /usr/bin/env node
'use strict'
const {generateReport} = require('./lib');

(async () => {
  await generateReport();
})();
