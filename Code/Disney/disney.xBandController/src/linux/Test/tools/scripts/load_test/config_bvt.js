var config = {
    pollDelay: 3000,
    facilities: {},
    protocol: 'http'
};
config.connection =  {
    server: '10.75.2.123',
    userName: 'QA_User',
    password: 'Crrctv11',
    options: {
        database: 'QA_XBRMS'
    }
};
config.hardware = {
    'spce-entry-hl': '10.110.1.96:8080'
};
(function() {
    var ips = [
        ['10.110.1.108', '8090'],
        ['10.110.1.108', '8090'],
        ['10.110.1.108', '8090'],
        ['10.110.1.108', '8090'],
        ['10.110.1.108', '8090'],

        ['10.110.1.108', '8090'],
        ['10.110.1.108', '8090'],
        ['10.110.1.108', '8090'],
        ['10.110.1.108', '8090'],

        ['10.110.1.108', '8090'],
        ['10.110.1.108', '8090'],

        ['10.110.1.112', '8090']
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

        ["80010114", "Buzz Lightyear's Space Ranger Spin", "Ride"],
        ["80010190", "Space Mountain", "Ride"],
	    
        ["11111111", "The Kiosk", "Kiosk"]
    ];
    facilities.forEach(function (facility, i) {
        config.facilities[facility[1]] = config.facilities[facility[0]] = ips[i];
        console.log(facility[1], ips[i].join(':'));
    });
})();

module.exports = config;
