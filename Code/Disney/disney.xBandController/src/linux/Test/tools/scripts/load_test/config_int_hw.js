var config = {
    pollDelay: 3000,
    facilities: {},
    protocol: 'http'
};
config.connection =  {
    server: '10.110.1.143',
    userName: 'QA_User',
    password: 'Crrctv11',
    options: {
        database: 'QA_XBRMS_INT'
    }
};
config.hardware = {
'ki-wp-l': '10.110.1.197:8080',
'ki-wp-2': '10.110.1.194:8080',
'ki-wp-3': '10.110.1.133:8080',
'ki-wp-4': '10.110.1.138:8080',
'ki-wp-5': '10.110.1.171:8080',
'ki-wp-6': '10.110.1.191:8080',
'ki-wp-7': '10.110.1.188:8080',
'hm-entry-l' : '10.110.1.199:8080',
'hm-merge-l' : '10.110.1.198:8080',
'jung-entry-l' : '10.110.1.196:8080',
'phil-entry-l' : '10.110.1.145:8080',
'phil-merge-l' : '10.110.1.141:8080',
'pooh-entry-l' : '10.110.1.190:8080',
'pooh-merge-l' : '10.110.1.182:8080',
'spce-entry-l' : '10.110.1.150:8080',
'spce-merge-l' : '10.110.1.152:8080',
'spl-entry-l' : '10.110.1.187:8080',
'spl-merge-l' : '10.110.1.192:8080'
};
(function() {
    var ips = [
        ['10.110.1.170', '8090'],
        ['10.110.1.170', '8090'],
        ['10.110.1.170', '8090'],
        ['10.110.1.170', '8090'],
        ['10.110.1.170', '8090'],

        ['10.110.1.170', '8090'],
        ['10.110.1.170', '8090'],
        ['10.110.1.170', '8090'],
        ['10.110.1.170', '8090'],
        
        ['10.110.1.170', '8090'],
        ['10.110.1.170', '8090'],

        ['10.110.1.174', '8090']
    ];
    var facilities = [
        ["80010153", "Jungle Cruise", "Ride"],
        ["80010170", "Mickey's PhilharMagic", "Movie"],
        ["80010176", "Peter Pan's Flight", "Ride"],
        ["80010213", "The Many Adventures of Winnie the Pooh", "Ride"],
        ["80010110", "Big Thunder Mountain Railroad", "Ride"],
        
        ["80010192", "Splash Mountain", "Ride"],
        ["80010208", "Haunted Mansion", "Ride"],
        ["15850196", "Mickey Meet and Greet", "Ride"],
        ["15850198", "Princess Meet and Greet", "Ride"],

        ["80010170", "Buzz Lightyear's Space Ranger Spin", "Ride"],
        ["80010190", "Space Mountain", "Ride"],
	    
        ["11111111", "The Kiosk", "Kiosk"]
    ];
    facilities.forEach(function (facility, i) {
        config.facilities[facility[1]] = config.facilities[facility[0]] = ips[i];
        console.log(facility[1], ips[i].join(':'));
    });
})();

module.exports = config;
