var config = {
    grid: '10.75.2.58',
    xbrc: {
        ip: '10.110.1.64',
        port: '8090',
        ui: '/UI/',
        locationListEdit: '/UI/locationlistedit',
        startOmniUrl: '/Xfpe/',
        url: '/Xfpe/showreaders?locationId=1&suiteId=2',
//        url: '/Xfpe/showreaders?locationId=1&suiteId=2&action=startTest',
        facility: '/UI/attractionview'
    }
};
var clearReset;
(function() {
    var wait = 75000;
    var timer;
    var snooze = function() {
        clearTimeout(timer);
        timer = setTimeout(reset, wait);
    };
    var reset = function() {
        client.end().init();
        snooze();
    };
    //snooze();
    clearReset = function() {
        clearTimeout(timer);
        snooze();
    };
})();
var webdriverjs = require("webdriverjs");
var colors = webdriverjs.colors;
var activity = setInterval(function() {
    var color = 'ltgray';
    var sep = '';
    if (!(activity.counter % 120)) {
        color = 'black';
        if (!(activity.counter % 480)) {
            color = 'blue';
            sep = '\n';
        }
    }
    process.stdout.write(sep + colors[color] + '•' + colors.reset);//console.log('.');
    activity.counter++;
}, 125);
activity.counter = 0;
//var client = webdriverjs.remote({ logLevel: 'silent' });
var options = {
//    desiredCapabilities:{ browserName: 'chrome' }, // to run in chrome
//    logLevel: 'silent',
    host: config.grid
};
var url = function(config, type) {
    return (config.protocol || 'http') + '://' + [config.ip, config.port].join(':') + config[type || 'url'];
};
var client = webdriverjs.remote(options); // to run it on a remote webdriver/selenium server
client.init();
process.on('exit', function () {
    console.log('exited');
    client.end();
});
process.title = 'node_webdriver';
var stdin = process.openStdin();
process.on('SIGINT', function () {
    console.log('calling client end');
    client.end();
    console.log('exiting');
    process.exit();
});
['10.110.1.64'/*, '10.110.1.68'*/].forEach(function(item) {
    var setIp = function() {
        console.log('\n======================  starting test for: ' + item + '  ============================');
//        config.xbrc.ip = item;
    };
    client.url(url(config.xbrc, 'ui'), function() {
        setIp();
        client.tests.titleEquals(/^xCoaster on/, 'Title of "' + config.xbrc.ui + '" starts with "xCoaster on"');
    });
    (function() {
        client.url(url(config.xbrc, 'startOmniUrl'));
        client.url(url(config.xbrc), function () {
            client.tests.titleEquals(/XBand Administration Console/, 'Title of "' + config.xbrc.url + '" is "XBand Administration Console"');
            client.pause(1000)
                .tests.textEquals('div.xfpeReader div.omniStatus p span', 'Connected', 'The first omni status text is "Connected"')
                .tests.textEquals('#idOmniStatus_entry-1', 'Connected', 'The #idOmniStatus_entry-1 omni status text is "Connected"')
        });
        client.url(url(config.xbrc, 'locationListEdit'), function () {
            client.tests.titleEquals(/XBand Administration Console/, 'Title of "' + config.xbrc.locationListEdit + '" is "XBand Administration Console"');
        });
        client.url(url(config.xbrc, 'facility'), function () {
            client.tests.titleEquals(/XBand Administration Console/, 'Title of "' + config.xbrc.facility + '" is "XBand Administration Console"');
        });
    })();
});
    client.end(function() {
        clearInterval(activity);
        clearReset();
        console.log('\ndone\n');
        process.exit();
    });
//client.url("https://github.com/")
//    .getElementSize("id", "header", function(result) {
//        console.log(result);
//    })
//    .getTitle(function(title) {
//        console.log(title)
//    })
//    .getElementCssProperty("id", "header", "color", function(result){
//        console.log(result);
//    })
//    .end();