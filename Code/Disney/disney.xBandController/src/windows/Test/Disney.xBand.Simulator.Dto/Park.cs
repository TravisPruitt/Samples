using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [XmlRoot]
    public class Park
    {
        [XmlElement(ElementName = "Facility")]
        Facility Facility { get; set; }
    }

    public class Facility
    {
        [XmlArray(ElementName = "GuestCapacities")]
        GuestCapacity[] GuestCapacities { get; set; }
    }

    public class GuestCapacity
    {
        [XmlAttribute(AttributeName="HourEnding")]
        public string HourEnding { get; set; }

        [XmlAttribute(AttributeName="xPassGuestsCarried")]
        public int xPassGuestsCarried { get; set; }

        [XmlAttribute(AttributeName="StandByGuestsCarried")]
        public int StandByGuestsCarried { get; set; }
    }
}
