using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace WDW.NGE.Support.Models.IDMS
{
    public class XbandAssociation
    {
        public String ExternalNumber { get; set; }
        public String LongRangeTag { get; set; }
        public String ShortRangeTag { get; set; }
        public String SecureId { get; set; }
        public String Uid { get; set; }
        public String PublicId { get; set; }
        public String PrintedName { get; set; }
        public String XbmsId { get; set; }
        public String BandType { get; set; }
        public String PrimaryState { get; set; }
        public String SecondaryState { get; set; }
        public String GuestIdType { get; set; }
        public String GuestIdValue { get; set; }
        public String XbandOwnerId { get; set; }
        public String XbandRequestId { get; set; }
    }
}
