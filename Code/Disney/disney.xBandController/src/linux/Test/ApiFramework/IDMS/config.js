var rdata = require("rdata");

module.exports = {
    url: 'http://localhost:8090/IDMS/',
    xband: {
        shortRangeTag: function() { return rdata.generate("^9", rdata.hex, 15)},
        longRangeTag: function() { return rdata.generate("^9", rdata.hex, 15)},
        publicId: function() { return rdata.generate("^9", rdata.hex, 15)},
        secureId: function() { return rdata.generate("^9", rdata.hex, 15)},
        bandId: function() { return rdata.generate("^9", rdata.hex, 15)}
    }
};
