using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;

namespace xBRCMessageUtil
{
    [XmlRootAttribute(ElementName = "jmsmessagerange", IsNullable = false)]
    public class MessageRange
    {
        [XmlElement("startDate")]
        public string StartDate { get; set; }

        [XmlElement("endDate")]
        public string EndDate { get; set; }
       
    }
}
