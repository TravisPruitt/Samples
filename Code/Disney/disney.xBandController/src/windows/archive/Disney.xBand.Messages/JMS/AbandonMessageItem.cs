using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace Disney.xBand.Messages.JMS
{
    public class AbandonMessageItem : SimpleMessageItem
    {
        [XmlElement("lastxmit")]
        public string LastTransmit { get; set; }
        
    }
}
