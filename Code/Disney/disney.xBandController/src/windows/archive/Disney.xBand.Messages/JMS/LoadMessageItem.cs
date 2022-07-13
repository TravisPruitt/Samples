using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace Disney.xBand.Messages.JMS
{
    public class LoadMessageItem : SimpleMessageItem
    {
        [XmlElement("carid")]
        public string CarID { get; set; }
       
        [XmlElement("waittime")]
        public int WaitTime { get; set; }

        [XmlElement("mergetime")]
        public int MergeTime { get; set; }
    }
}
