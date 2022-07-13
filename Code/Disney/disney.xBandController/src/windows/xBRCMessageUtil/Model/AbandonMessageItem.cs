using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;

namespace xBRCMessageUtil
{
    [XmlRootAttribute(ElementName = "message", IsNullable = false)]
    public class AbandonMessageItem : SimpleMessageItem
    {
        [XmlElement("lastxmit")]
        public string LastTransmit { get; set; }
        
    }
}
