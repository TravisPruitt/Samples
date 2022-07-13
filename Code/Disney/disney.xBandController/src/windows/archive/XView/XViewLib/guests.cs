using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XViewLib
{
    public partial class guest
    {
        private guest_info _guestInfo;
        private List<xband> _xbands;

        public guest_info guest_info
        {
            get { return _guestInfo; }
            set { _guestInfo = value; }
        }


        public List<xband> xbands
        {
            get { return _xbands; }
            set { _xbands = value; }
        }
    }
}
