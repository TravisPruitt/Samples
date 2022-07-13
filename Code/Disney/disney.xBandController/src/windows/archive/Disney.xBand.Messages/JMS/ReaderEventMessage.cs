using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace Disney.xBand.Messages.JMS
{
    public class ReaderEventMessage : MessageBase
    {
        
        [XmlElement(ElementName = "message", Type=typeof(ReaderEventMessageItem))]
        public ReaderEventMessageItem Message { get; set; }
 
     }

}
