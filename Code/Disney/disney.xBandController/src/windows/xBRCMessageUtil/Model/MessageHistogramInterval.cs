using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;

namespace xBRCMessageUtil
{
    [XmlRootAttribute(ElementName = "interval", IsNullable = false)]
    public class MessageHistogramInterval
    {
        [XmlElement(ElementName = "year")]
        public int Year { get; set; }

        [XmlElement(ElementName = "month")]
        public int Month { get; set; }

        [XmlElement(ElementName = "day")]
        public int Day { get; set; }

        [XmlElement(ElementName = "hour")]
        public int Hour { get; set; }

        [XmlElement(ElementName = "count")]
        public int Count { get; set; }
    }
}
