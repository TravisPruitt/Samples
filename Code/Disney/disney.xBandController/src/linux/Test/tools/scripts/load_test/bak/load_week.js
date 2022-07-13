var Connection = require('tedious').Connection;
var Request = require('tedious').Request;
//var http = require('http');
var request = require('request');
var config = {
    pollDelay: 10000,
    facilities: {}
};
var facility = function(key) {
    return config.facilities[key].join(':');
};
(function() {
/*    var ips = [['10.110.1.68', '8090'], ['10.110.1.68', '8090'], ['10.110.1.68', '8090'], ['10.110.1.68', '8090'], ['10.110.1.68', '8090'],
        ['10.110.1.69', '8090'], ['10.110.1.69', '8090'], ['10.110.1.69', '8090'], ['10.110.1.69', '8090'], ['10.110.1.69', '8090'], ['10.110.1.69', '8090'], ['10.110.1.69', '8090']];*/
    var ips = [['10.110.1.63', '8090'], ['10.110.1.63', '8090'], ['10.110.1.63', '8090'], ['10.110.1.63', '8090'], ['10.110.1.63', '8090'],
        ['10.110.1.66', '8090'], ['10.110.1.66', '8090'], ['10.110.1.66', '8090'], ['10.110.1.66', '8090'], ['10.110.1.66', '8090'], ['10.110.1.66', '8090'], ['10.110.1.66', '8090']];
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
})();
var connection;
(function() {
    var options = {
        server: '10.75.3.211',
        userName: 'QA_User',
        password: 'Crrctv11',
        options: { database: 'QA_XBRMS' }
//        options: { database: 'QA_XBRMS', packetSize: 8 * 1024, debug: { packet: true, debug: true, log: true } }
        /**
         ,options: {
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
            // If no error, then good to go...
            console.log(err || 'ok');
//            new Request('SET TEXTSIZE 65536', executeStatement);
            executeStatement();
            //http.request('');
        }
    );

    connection.on('debug', function(text) {
            console.log(text);
        }
    );
})();

function executeStatement() {
//    request = new Request("select 42, 'hello world'", function(err) {
//    request = new Request("select top 10 * from [QA-XBRMS]Facilities", function(err) {
//    request = new Request("[QA-XBRMS].sp_getSimEventsJSON", function(err) {
//    request = new Request("select * from Facilities", function(err) {
//    var query = new Request("dbo.sp_resetSimEvents", function(err) {
    var query = new Request(process.argv[2] == 'reset' ? "dbo.sp_resetSimEventsWeek" : "dbo.sp_getSimEventsJSON_Week", function(err) {
//    request = new Request("dbo.sp_Facilities", function(err) {
//    request = new Request("select top 10 * from SimDayInPark", function(err) {
        console.log(err || 'still ok');
//        connection.close();todo:bring this back from exit?
    });

    query.on('row', function(columns) {
        console.log('=============================');
        columns.forEach(function(column) {
            if (column.value === null) {
                console.log('NULL');
            } else {
//                for(var k in column.metadata) {
//                    console.log(k);
//                }
                var value = function(row, key) {
//                    console.log(key, row[map[key]]);
                    return row[map[key]];
                };
                var url = function(row) {
                    return ['http://', facility(value(row, 'FacilityID')) , '/Xfpe/restful/', value(row, 'EventTypeName').toLowerCase(), '-', value(row, 'ReaderPosition'), '/rfid/tap?uid=', value(row, 'xbandId')].join('');//&pid=12312312&sid=12312312312
                };
                try {
                    console.log('length: ', column.value.length);
                    var data = eval(column.value/*.replace(/^\[\[\[/, '[[')*/);
//                console.log('data', data);
                    console.log(data.length || -1);
                    var headers = data.shift();
                    var map = {};
                    headers.forEach(function(column, i) {
                        map[column] = i;
                    });
                    data.forEach(function(row) {
//                    console.log(row[map.ReaderPosition]);
                        var u = url(row);
                        console.log('fetching: ' + u, value(row, 'LocalOffset'));
                        setTimeout(function() {
                            request(u, function(err) {
//                            console.log(err || 989);
                                console.log('retrieved', u);
                            });
                        }, value(row, 'LocalOffset') * 1);
                    });
                } catch(ex) {
                    console.log('big fail', ex);
                }
                setTimeout(executeStatement, config.pollDelay);
                console.log(column.metadata.colName + ': ' + column.value);
            }
        });
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
