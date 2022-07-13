var request = require('request');
var jsdom = require('jsdom');
Array.fromArguments = function(args) {
    var r = [];
    Array.prototype.forEach.apply(args, [function(item) {
        r.push(item);
    }]);
    return r;
};
var info = 'info-----';
var error = '\nerror-----\n\n';
var errorEnd= '\n\n-----error\n\n';
var indent = '\t\t';
var green = 'Green';
var red = '\nRED\n\n';
var test = module.exports = {
    ok: function(message) {
         console.log('\n');
         console.log(green, ['"', message, '"'].join(''), '\n');
         test.greens++;
    },
    fail: function(message) {
        console.log('\n');
        console.log(red, ['"', message, '"'].join(''), '\n');
        test.failures.push(message);
        test.reds++;
    },
    error: function() {
        var args = Array.fromArguments(arguments);
        args.unshift(error);
        args.push((new Date() - test.startTime)/1000 + 's');
        args.push(errorEnd);
        console.log.apply(null, args);
    },
    info: function() {
        var args = Array.fromArguments(arguments);
        args.unshift(info);
        args.unshift(indent);
        args.push((new Date() - test.startTime)/1000 + 's');
        console.log.apply(null, args);
    },
    remote: function(url /*, callbacks*/) {
        test.startTime = new Date();
        test.jobs = test.greens = test.reds = test.noReply = 0;
        test.failures = [];
        test.info('start:', test.startTime);
        test.remote = function(url/*, callbacks*/) {
            var options = url;
            if (typeof url === typeof '') {
                options = { url: url };
            }
            url = options.url;
            test.info('fetching:', url);
            var tests = Array.fromArguments(arguments);
            tests.shift();
            request(options, function(err, window, body) {
                if (err) {
                    test.noReply++;
                    test.error(url, err);
                } else {
                    test.info('retrieved:', url);
                    try {
                        var data;
                        var runTests = function(data) {
                            tests.forEach(function(t) {
                                try {
                                    test.jobs++;
                                    test.info('running test:', url);
                                    console.log('\n');
                                    console.log(green, ['"', t(data, window), '"'].join(''), '\n');
                                    test.info('finished test:', url);
                                } catch (ex) {
                                    if (ex.name === 'AssertionError') {
                                        delete ex.name;
                                    }
                                    test.error(red, ex);
                                }
                            });
                        };

                        if (/text\/plain/.test(window.headers['content-type'])) {
                            runTests(body);
                        }
                        else if (/^[\s\n]?\</.test(body)) { //xml or html
                            jsdom.env({
                                html: body,
                                features: {
                                    QuerySelector: true,
                                    FetchExternalResources: false,
                                    ProcessExternalResources: false
                                },
                                done: function(err, window) {
                                    if (err) {
                                        test.error('jsdom:', url, err);
                                    } else {
                                        runTests(window);
                                    }
                                }
                            });
                        } else {
                            if (body !== undefined)
                            {
                                if (body)
                                {
                                    console.log("body is " + body);
                                }
                                runTests(JSON.parse(body || '{ empty: true }'));
                            }
                            else
                                runTests();
                        }
                    } catch (ex) {
                        test.info('parse error:\n', ex, '\n\n ', window.body);
                    }
                }
            });
        };
        test.remote.apply(test, Array.fromArguments(arguments));
    }
};
(function() {
    var apply = function(args) {
        test.remote.apply(test, Array.fromArguments(args));
    };
    test.remote.get = function(options) {
        apply(arguments);
    };
    test.remote.put = function(options) {
        options.method = 'PUT';
        apply(arguments);
    };
    test.remote.post = function(options) {
        options.method = 'POST';
        apply(arguments);
    };
    test.remote.del = function(options) {
        options.method = 'DELETE';
        apply(arguments);
    };
    process.on('exit', function() {
        console.log('\n\n\n=====================');
        test.failures.forEach(function(message) {
            console.log('failed: ' + message);
        });
        console.log('passed:', test.greens);
        console.log('failed:', test.reds);
        console.log('no response:', test.noReply);
        var now = new Date();
        console.log('total time:', (now - test.startTime)/1000 + 's');
        console.log(indent, now);
        console.log('=====================');
    })

})();
