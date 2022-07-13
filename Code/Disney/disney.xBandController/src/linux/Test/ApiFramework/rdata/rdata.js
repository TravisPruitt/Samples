
rdata = module.exports =  {

    string: function(length, characters) {
        var answer = '';
        for (var i = 0; i < length; i++)
        {
            answer += characters[Math.floor(Math.random() * characters.length)];
        }
        return answer;
    },
    hex: function(length) {
        return rdata.string(length, '0123456789ABCEDF');
    },
    element: function(array) {
        return array[Math.floor(Math.random() * array.length)];
    },
    value: function(min, max) {
        var value = 0;
        var range = 0;

        if (min > max) {
            var tmp = min;
            min = max;
            max = tmp;
        }

        range = (max - min) + 1;

        value = Math.floor(Math.random() * (range + 1)) + min;

        return value;
    },
    date: function(minYear, minMonth, maxYear, maxMonth) {
        var monthString = "";
        var dayString = "";

        if (false) {
            var year = rdata.value(minYear, maxYear);
            var month = rdata.value(minMonth, maxMonth);
            var day;
            if (month == 2) // February
                day  = rdata.value(1, 28); // Forget leap year
            else if ([1, 3, 5, 7, 8, 10, 12].indexOf(month) != -1 )
                day = rdata.value(1, 31);
            else
                day = rdata.value(1, 30);
        } else {
            year = 1980;
            month = 3;
            day = 15;
        }

        if (month < 10)
            monthString = "0";
        monthString += month;

        if (day < 10)
            dayString = "0";
        dayString += day;

        return [year, monthString, dayString].join('-');
    },
    generate: function() {
        var args = [];
        Array.prototype.push.apply(args, arguments);
        var filterPattern = args.shift();
        var generatorFunction = args.shift();

        var regexpPattern = new RegExp(filterPattern);
        if (regexpPattern == null) {
            console.log("problem");
        }

        var answer;
        var regexpArray;
        do {
            answer = generatorFunction(args);
            regexpArray = regexpPattern.exec(answer);
        } while (regexpArray == null);
        return answer;
    }
};
