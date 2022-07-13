using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace Disney.xBand.xBMS.Simulator.Dto
{
    [XmlRootAttribute(ElementName = "SeedData", IsNullable = false)]
    public class SeedData
    {
        [XmlElement(ElementName = "TravelSegmentId", Order = 1)]
        public long TravelSegmentId { get; set; }

        [XmlElement(ElementName = "TravelComponentId", Order = 2)]
        public long TravelComponentId { get; set; }

        [XmlElement(ElementName = "TravelPlanId", Order = 3)]
        public long TravelPlanId { get; set; }

        [XmlElement(ElementName = "TransactionalGuestId", Order = 4)]
        public long TransactionalGuestId { get; set; }

        [XmlElement(ElementName = "SecureId", Order = 5)]
        public long SecureId { get; set; }

        [XmlElement(ElementName = "PublicId", Order = 6)]
        public long PublicId { get; set; }

        [XmlElement(ElementName = "ShortRangeTag", Order = 7)]
        public long ShortRangeTag { get; set; }

        [XmlElement(ElementName = "ExternalNumber", Order = 8)]
        public long ExternalNumber { get; set; }
    }
}
