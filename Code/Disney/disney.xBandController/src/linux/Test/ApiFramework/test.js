var test = require('test-core');
var assert = require('assert');
var config = require('./config.js');
var baseUrl = config.xbrc.base;
test.remote(baseUrl + 'status',
    function(window) {
        var expected = 10;
        var message = 'there are at least ' + expected + ' max elements';
        assert.ok(window && window.document.querySelectorAll('max').length > expected, message);
        var max = window.document.querySelector('max');
        message = 'max is 0.0';
        assert.equal(max.firstChild.nodeValue, '0.0', message);
        return message;
    },
    function(window) {
        var message = 'status red';
        var node = window && window.document.querySelector('status');
        var status = node.firstChild.nodeValue;
        assert.equal(status, 'Red', message);
        return message;
    });
test.remote({
        url: config.xfp.base + 'rfid/tap?uid=123&pid=456',
        headers: {
            'User-Agent': 'AppleWebKit/534.24 (KHTML, like Gecko)'
        }
    },
    function(window) {
        return 'tapped';
    });
test.remote(baseUrl + 'messages?max=2',
    function(window) {
        var message = '2 messages';
        var nodes = window && window.document.querySelectorAll('message');
        assert.equal(nodes.length, 2, message);
        return message;
    });
test.remote(baseUrl + 'readerlocationinfo',
    function(window) {
        var message = 'reader location info';
        var node = window && window.document.querySelector('readerlocationinfo');
        assert.equal(node.tagName.toLocaleLowerCase(), 'readerlocationinfo', message);
        return message;
    });
test.remote(baseUrl + 'readerlocationinfo/name/Entry',
    function(window) {
        var message = 'one reader location info entry';
        var nodes = window && window.document.querySelectorAll('readerlocationinfo readerlocation');
        assert.equal(nodes.length, 1, message);
        return message;
    });
test.remote(baseUrl + 'configurations',
    function(data) {
        var expected = 1;
        var message = 'more than ' + expected + ' configuration';
        assert.ok(data.configurations.length > expected, message);
        return message;
    });
test.remote(baseUrl + 'currentconfiguration',
    function(window) {
        var expected = 1;
        var message = 'more than ' + expected + ' property in current configuration';
        var nodes = window && window.document.querySelectorAll('property');
        assert.ok(nodes.length > expected, message);
        return message;
    });
