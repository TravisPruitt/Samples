using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using xBRCMessageUtil.Model.Model;

namespace xBRCMessageUtil
{
    [XmlRootAttribute(ElementName = "guest", IsNullable = false)]
    public class GuestState
    {
        [XmlElement("id")]
        public string GuestId { get; set; }

        [XmlElement("state")]
        public string State { get; set; }

        [XmlElement("xpass")]
        public bool XPass{ get; set; }

        [XmlElement("location")]
        public LocationInfo Location { get; set; }
    }
}
