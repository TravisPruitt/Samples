using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.disney.xband.xbrc.XBRCInfo;

namespace com.disney.xband.xbrc.MobileGxpTest
{
    class FacilityInfo
    {
        private Facility f;

        public FacilityInfo(Facility f)
        {
            this.f = f;
        }

        public Facility facility
        {
            get
            {
                return f;
            }
        }

        public override string ToString()
        {
            return f.name + "(" + f.id + ")";
        }
    }
}
