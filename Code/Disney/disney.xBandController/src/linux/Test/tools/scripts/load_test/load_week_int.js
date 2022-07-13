var Connection = require('tedious').Connection;
var Request = require('tedious').Request;
var request = require('request');
var config = require('./config_int.js');
console.log(config.facilities['Space Mountain']);

var facility = function(key) {
    return (config.facilities[key] || ['no_key', key]).join(':');
};
(function() {
    if (!config.facilities) {
        /*    var ips = [['10.110.1.68', '8090'], ['10.110.1.68', '8090'], ['10.110.1.68', '8090'], ['10.110.1.68', '8090'], ['10.110.1.68', '8090'],
         ['10.110.1.69', '8090'], ['10.110.1.69', '8090'], ['10.110.1.69', '8090'], ['10.110.1.69', '8090'], ['10.110.1.69', '8090'], ['10.110.1.69', '8090'], ['10.110.1.69', '8090']];*/
        var ips = [['10.110.1.93', '8090'], ['10.110.1.93', '8090'], ['10.110.1.93', '8090'], ['10.110.1.93', '8090'], ['10.110.1.93', '8090'],
            ['10.110.1.96', '8090'], ['10.110.1.96', '8090'], ['10.110.1.96', '8090'], ['10.110.1.96', '8090'], ['10.110.1.96', '8090'], ['10.110.1.96', '8090'], ['10.110.1.96', '8090']];
        var facilities = [["80010153", "Jungle Cruise", "Ride"],
            ["80010170", "Mickey's PhilharMagic", "Movie"],
            ["80010176", "Peter Pan's Flight", "Ride"],
            ["80010213", "The Many Adventures of Winnie the Pooh", "Ride"],
            ["80010110", "Big Thunder Mountain Railroad", "Ride"],
            ["80010192", "Splash Mountain", "Ride"],
            ["80010208", "Haunted Mansion", "Ride"],
            ["15907813", "Town Square Theater", "Sub-Land"],
            ["80010114", "Buzz Lightyear's Space Ranger Spin", "Ride"],
            ["80010190", "Space Mountain", "Ride"]];
        facilities.forEach(function (facility, i) {
            config.facilities[facility[1]] = config.facilities[facility[0]] = ips[i];//.join(':');
            console.log(facility[1], ips[i].join(':'));
        });
    }
})();

var hardwareReaders = [
	'big-entry-l',
	'test-entry-r',
	'test-merge-r'
	];

var connection;
(function() {
    var options = config.connection || {
        server: '10.75.3.999',
//        userName: 'QA_User',
        userName: 'QA_User',
        password: 'Crrctv11',
        options: { database: 'QA_XBRMS' }
        /**
         options: {
           database: 'QA_XBRMS',
           debug: {
             packet: true,
             data: true,
             payload: true,
             token: false,
             log: true
           }
         }
         //*/
    };
    connection = new Connection(options);
    connection.on('connect', function(err) {
            console.log(err || 'ok');
            var run = err || executeStatement();
        }
    );
})();
function executeStatement() {
    var query = new Request(process.argv[2] == 'reset' ? "dbo.sp_resetSimEventsWeek" : "dbo.sp_getSimEventsJSON_Week", function(err) {
        console.log(err || 'still ok');
//        connection.close();todo:bring this back from exit?
    });
    query.on('row', function(columns) {
        console.log('=============================');
        columns.forEach(function(column) {
            if (column.value === null) {
                console.log('NULL');
            } else {
                var value = function(row, key) {
                    return row[map[key]];
                };
		var events = { 'E': 'entry', 'M': 'merge', 'W': 'wp' };
                var url = function(row) {
                    //return ['http://', facility(value(row, 'FacilityID')) , '/Xfpe/restful/', value(row, 'EventTypeName').toLowerCase(), '-', value(row, 'ReaderPosition'), '1/rfid/tap?uid=', value(row, 'xbandId')].join('');//&pid=12312312&sid=12312312312
		    var reader=([value(row, 'ReaderName').toLowerCase(), events[value(row, 'EventTypeName')], value(row, 'ReaderPosition').toLowerCase()].join('-'));
					if(isHardwareReader(reader)) {
						//Hardware xTP restful call example:
						//http://10.110.1.110:8080/rfid/tap?uid=54E03ABD32103323&pid=123456&sid=890ABC
						return [config.protocol, '://', config.hardware[reader], '/rfid/tap?uid=', value(row, 'tapid'), '&pid=123456', '&sid=', value(row, 'xbandId')].join('');					
					}
					else {
						//Software xTP restful call example:
						//http://10.110.1.63:8090/Xfpe/restful/phil-entry-l/rfid/tap?uid=F0B2E8E487A800
						return [config.protocol, '://', facility(value(row, 'FacilityID')) , '/Xfpe/restful/', reader, '/rfid/tap?uid=', value(row, 'tapid'), '&sid=', value(row, 'xbandId')].join('');
					}
                };
                try {
                    console.log('length: ', column.value.length);
                    var data = eval(column.value/*.replace(/^\[\[\[/, '[[')*/);
                    console.log(data && data.length || -1);
                    if (data) {
                        var headers = data.shift();
                        var map = {};
                        headers.forEach(function(column, i) {
                            map[column] = i;
                        });
                        data.forEach(function(row) {
                            var u = url(row);
                            console.log('fetching: ' + u, value(row, 'LocalOffset'));
                            setTimeout(function() {
                                request(u, function(err) {
                                    console.log('retrieved', u);
                                });
                            }, value(row, 'LocalOffset') * 1000);
                        });
                    }
                } catch(ex) {
                    console.log('big fail', ex);
                }
                console.log(column.metadata.colName + ': ' + column.value);
            }
        });
        setTimeout(executeStatement, config.pollDelay);
    });
    query.on('done', function(rowCount, more) {
        console.log(more + ' more');
        console.log(rowCount + ' rows returned');
    });
    process.on('SIGINT', function () {
        console.log('calling close connection');
        connection.close();
        console.log('exiting');
        process.exit();
    });
    connection.execSql(query);
}

function isHardwareReader(readerName) {
    return config.hardware[readerName];
	var retVal = false;
	hardwareReaders.forEach(function(hwReader) {
		if(hwReader == readerName) {
			//console.log(hwReader, 'is a hardware reader');
			retVal = true;
		}
	});
	return retVal;
}
