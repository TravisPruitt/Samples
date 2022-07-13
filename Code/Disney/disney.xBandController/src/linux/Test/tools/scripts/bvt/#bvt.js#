var showProgressBar = false;

var config = {
    grid: '10.75.2.58',
    xbrc: {
        ip: '10.110.1.64',
        port: '8090',
        ui: '/UI/',
        locationListEdit: '/UI/locationlistedit',
        startOmniUrl: '/Xfpe/',
        url: '/Xfpe/showreaders?locationId=1&suiteId=2',
        facility: '/UI/attractionview'
    },
	 xbrcA: {
        ip: '10.110.1.68',
        port: '8090',
        ui: '/UI/',
        locationListEdit: '/UI/locationlistedit',
        facility: '/UI/attractionview'
    },
	xbrms: {
	    ip: '10.110.1.65',
		port: '8080',
		ui: '/XBRMS/'
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
        color = 'yellow';
        if (!(activity.counter % 480)) {
            color = 'blue';
            sep = '\n';
        }
    }
	if(showProgressBar == true) {
		process.stdout.write(sep + colors[color] + '•' + colors.reset);//console.log('.');
	}
    activity.counter++;
}, 125);
activity.counter = 0;
//var client = webdriverjs.remote({ logLevel: 'silent' });
var options = {
//    desiredCapabilities:{ browserName: 'chrome' }, // to run in chrome
    logLevel: 'silent',
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
try {
	process.on('SIGINT', function () {
    console.log('calling client end');
    client.end();
    console.log('exiting');
    process.exit();
});
} catch (ex) {}

//Run the xBRMS tests        
console.log('\n======================  starting test for xBRMS: ' + config.xbrms.ip + '  ============================');
//client.url(url(config.xbrms, 'ui'));
//client.tests.titleEquals(/Mozilla Firefox/, 'Title of "' + config.xbrms.ui + '" is "XBand Administration Console"');
//console.log(client.protocol.title);

client.url(url(config.xbrms, 'ui'), function() {
            //Check the title of the page
		    client.tests.titleEquals('', 'Title of "' + config.xbrms.ui + '" is empty');
			client.tests.textEquals('div.round.lightBackground ul li a', 'XBRC Systems Health', 'Found "XBRC Systems Health" Link');
			client.tests.textEquals('div.round.lightBackground ul li + li a', 'Guest Search', 'Found "Guest Search" Link');
			client.tests.textEquals('div.round.lightBackground ul li + li + li a', 'XBRC Configuration Edit', 'Found "XBRC Configuration Edit" Link');
			
        });

//Run the xBRC PE tests
[config.xbrc.ip].forEach(function(item) {
    var setIp = function() {
        console.log('\n======================  starting test for xBRC: ' + item + '   ============================');
        config.xbrc.ip = item;
    };
    client.url(url(config.xbrc, 'ui'), function() {
        setIp();
        //client.tests.titleEquals(/^xCoaster on/, 'Title of "' + config.xbrc.ui + '" starts with "xCoaster on"');
		//Check the title of the xBRC UI page
		client.tests.titleEquals(/^(\w*) on (\w*)$/, 'Title of "' + config.xbrc.ui + '" matches the pattern: {word} on {word}"');
    });
    (function() {
	    //hit the reader emulator page to 'wake' omni 
        client.url(url(config.xbrc, 'startOmniUrl'));
		
		//Load the xFP reader simulator page
        client.url(url(config.xbrc), function() {
            //Check the title of the xFP reader simulator page
			client.tests.titleEquals(/XBand Administration Console/, 'Title of "' + config.xbrc.url + '" is "XBand Administration Console"');
            //Wait for the omni status to populate
			client.pause(2000);
			//Check that the first omni reader is showing 'Connected' status
			client.tests.textEquals('div.xfpeReader div.omniStatus p span', 'Connected', 'Reader #1 omni status text is "Connected"')
            //The old test that may fail depending on the reader configuration
			client.tests.textEquals('#idOmniStatus_entry-2', 'Connected', 'Reader #2 omni status text is "Connected"')
			client.tests.textEquals('#idOmniStatus_entry-3', 'Connected', 'Reader #3 omni status text is "Connected"')
			client.tests.textEquals('#idOmniStatus_entry-4', 'Connected', 'Reader #4 omni status text is "Connected"')	
        });
		
		//Load the xBRC location editor page
        client.url(url(config.xbrc, 'locationListEdit'), function() {
            //Check the title of the page
			client.tests.titleEquals(/XBand Administration Console/, 'Title of "' + config.xbrc.locationListEdit + '" is "XBand Administration Console"');
        });
		
		//Load the xBRC attraction view
        client.url(url(config.xbrc, 'facility'), function() {
            //Check the title of the page
		    client.tests.titleEquals(/XBand Administration Console/, 'Title of "' + config.xbrc.facility + '" is "XBand Administration Console"');
        });
    })();
});

//Run the xbrc Attraction tests
[config.xbrcA.ip].forEach(function(item) {
    var setIp = function() {
        console.log('\n======================  starting test for xBRC: ' + item + '   ============================');
        config.xbrc.ip = item;
    };
    client.url(url(config.xbrcA, 'ui'), function() {
        setIp();
        //client.tests.titleEquals(/^xCoaster on/, 'Title of "' + config.xbrc.ui + '" starts with "xCoaster on"');
		//Check the title of the xBRC UI page
		client.tests.titleEquals(/^(\w*) on (\w*)$/, 'Title of "' + config.xbrcA.ui + '" matches the pattern: {word} on {word}"');
		
		//Check the links
		client.tests.textEquals('div.round.lightBackground ul li a', 'Marching Guests', 'Found "Marching Guests" Link');
		//client.tests.textEquals('div.round.lightBackground ul li + li a', 'Facility View', 'Found "Marching Guests" Link');
		//client.tests.textEquals('div.round.lightBackground ul li a + li a + li a', 'Facility Designer', 'Found "Marching Guests" Link');
		//client.tests.textEquals('div.round.lightBackground ul li a + li a + li a + li a', 'Location Editor', 'Found "Marching Guests" Link');
    });
    (function() {
		//Load the xBRC location editor page
        client.url(url(config.xbrcA, 'locationListEdit'), function() {
            //Check the title of the page
			client.tests.titleEquals(/XBand Administration Console/, 'Title of "' + config.xbrcA.locationListEdit + '" is "XBand Administration Console"');
        });
		
		//Load the xBRC attraction view
        client.url(url(config.xbrcA, 'facility'), function() {
            //Check the title of the page
		    client.tests.titleEquals(/XBand Administration Console/, 'Title of "' + config.xbrcA.facility + '" is "XBand Administration Console"');
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