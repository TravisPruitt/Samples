using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.Provisionator.Dto
{
    public class Guest
    {
        public string FirstName { get; set; }

        public string LastName { get; set; }

        public long GuestID { get; set; }

        public string xID { get; set; }

        public List<MagicBand> MagicBands { get; set; }
    }
}
