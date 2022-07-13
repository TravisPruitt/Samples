using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;
using System.Xml.Serialization;

namespace Disney.xBand.xBMS.Simulator.Dto
{
    [XmlRootAttribute(ElementName = "businessEvent", IsNullable = false, Namespace="")]
    public class BusinessEvent
    {
        [XmlIgnore]
        public string Payload { get; set; }

        [XmlElement(ElementName = "location", Order = 1)]
        public String Location { get; set; }

        [XmlElement(ElementName = "eventType", Order = 2)]
        public String EventType { get; set; }

        [XmlElement(ElementName = "subType", Order = 3)]
        public String SubType { get; set; }

        [XmlElement(ElementName = "referenceId", Order = 4)]
        public String ReferenceId { get; set; }

        [XmlElement(ElementName = "guestIdentifier", Order = 5)]
        public String GuestIdentifier { get; set; }

        [XmlElement(ElementName = "timeStamp", Order = 6)]
        public String TimeStamp { get; set; }

        [XmlElement(ElementName = "payload", Order = 7)]
        public System.Xml.XmlCDataSection PayloadCData
        {
            get
            {
                return new System.Xml.XmlDocument().CreateCDataSection(Payload);
            }
            set
            {
                Payload = value.Value;
            }
        }
        [XmlElement(ElementName = "correlationId", Order = 8)]
        public String CorrelationId { get; set; }
    }
}
