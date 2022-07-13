using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace Disney.xBand.Messages.JMS
{
    [XmlRootAttribute(ElementName = "venue", IsNullable = false)]
    public class MessageBase
    {
        [XmlAttribute(AttributeName="name")]
        public string FacilityName { get; set; }

        [XmlIgnore()]
        public string FacilityTypeName { get; set; }

    }
}
