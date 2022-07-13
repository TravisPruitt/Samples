using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;

namespace xBRCMessageUtil
{
    [XmlRootAttribute(ElementName="jmsmessagehistogram", IsNullable=false)]
    public class MessageHistogram
    {
        [XmlElement(ElementName="grouping")]
        public string Grouping { get; set; }

        [XmlElement(ElementName = "interval")]
        public MessageHistogramInterval[] Intervals { get; set; }
    }
}
