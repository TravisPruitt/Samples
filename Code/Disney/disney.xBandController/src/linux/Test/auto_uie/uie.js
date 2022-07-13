var test = require('test-core');
var assert = require('assert');
var config = require('./config.js');
var count = (process.argv[2] || 500) * 1;
var total = count;
var once = function() {
    test.remote(config.idms.base + 'status' + '?count=' + (total - count),
        function(window) {
            var expected = 'Green';
            var message = 'status is ' + expected;
            assert.equal(window && window.document.querySelector('status status').firstChild.nodeValue, expected, message);
            return message;
        });
    return --count;
};
while(once()){}