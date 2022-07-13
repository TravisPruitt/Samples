using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace Disney.xBand.Messages.JMS
{   
    public class SimpleMessageItem
    {
        [XmlAttribute("type")]
        public string MessageType { get; set; }

        [XmlAttribute("time")]
        public string Timestamp { get; set; }

        [XmlElement("guestid")]
        public string GuestID { get; set; }

        [XmlElement("xpass")]
        public bool xPass { get; set; }

        [XmlElement("readerlocation")]
        public string ReaderLocation { get; set; }
    }
}
