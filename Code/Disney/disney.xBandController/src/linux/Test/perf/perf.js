var jsdom = require('jsdom');
var activity = setInterval(function() {
//    var color = 'ltgray';
    var sep = '';
//    if (!(activity.counter % 120)) {
//        color = 'black';
        if (!(activity.counter % 480)) {
//            color = 'blue';
            sep = '\n';
        }
//    }
    process.stdout.write('•');//console.log('.');
    activity.counter++;
}, 125);
//var doc = jsdom.env({ html: '<html><body><guestid>foo</guestid></body></html>', features: { QuerySelector: true }, done: function(err, window) {
//var doc = jsdom.env({ html: 'http://10.110.1.68:8080/messages', features: { QuerySelector: true }, done: function(err, window) {
Array.forEach = function() {
    Array.prototype.forEach.call(arguments[0], arguments[1]);
};
var jsonMaker = function(selectorFunc) {
    return function() {
        var r = {};
        Array.forEach(arguments, function(item) {
            r[item] = selectorFunc(item).value;
        });
        return r;
    };
};
var statusReader = function(ip, last) {
    return function(err, window) {
        console.log('fetched: ' + ip);
        if(window && window.document) {
            var qs = function(selector) {
                var element = window.document.querySelector(selector);
                return (element && { element: element, value: (element.firstChild && element.firstChild.nodeValue) || '' }) || {} ;
            };
            var status = jsonMaker(qs)('JMSBroker',
                'lastMessageSeq',
                'lastMessageToJMS',
                'lastMessageToUpdateStream',
                'messageCount',
                'model',
                'perfMetricsPeriod',
                'readerLocationsCount',
                'readerTestMode',
                'startPerfTime',
                'status',
                'statusMessage',
                'version');
            status.ip = ip;
            var venue = window.document.querySelector('VENUE');
            status.name = venue && venue.getAttribute('name');
            status.metrics = {};
            Array.forEach(window.document.getElementsByTagName('*'), function(element) {
                var tn = element._tagName;
                if (/^perf/i.test(tn) && !{ perfMetricsPeriod: 1 }[tn]) {
                    status.metrics[tn.replace(/^PERF/, 'perf')] = jsonMaker(function(selector) {
                        var child = element.querySelector(selector);
                        return (child && { element: child, value: (child.firstChild && child.firstChild.nodeValue) || '' }) || {} ;
                    })('MAX', 'MEAN', 'MIN');
                }
            });
            console.log(status);
            if (last) {
                clearInterval(activity);
            }
        }
    };
};
var ip = '10.110.1.64';
var doc = jsdom.env({ html: 'http://' + ip + ':8080/status', features: { QuerySelector: true }, done: statusReader(ip) });
ip = '10.110.1.68';
var doc2 = jsdom.env({ html: 'http://' + ip + ':8080/status', features: { QuerySelector: true }, done: statusReader(ip, true) });
//console.log(doc);
