using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.disney.xband.xbrc.XBRCInfo;

namespace com.disney.xband.xbrc.MobileGxpTest
{
    class LocationItem
    {
        private LocationInfo li;

        public LocationItem(LocationInfo li)
        {
            this.li = li;
        }

        public LocationInfo locationInfo
        {
            get
            {
                return li;
            }
        }

        public override string ToString()
        {
            return li.name + "(" + li.id + ")";
        }
    }
}
