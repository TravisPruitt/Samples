using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;

namespace xBRCMessageUtil
{
    [XmlRootAttribute(ElementName = "message", IsNullable = false)]
    public class SimpleMessageItem
    {
        [XmlAttribute("type")]
        public string MessageType { get; set; }

        [XmlAttribute("time")]
        public string Timestamp { get; set; }

        [XmlElement("messageid")]
        public long MessageId { get; set; }

        [XmlElement("guestid")]
        public string GuestID { get; set; }

        [XmlElement("xpass")]
        public bool xPass { get; set; }

        [XmlElement("readerlocation")]
        public string ReaderLocation { get; set; }
    }
}
