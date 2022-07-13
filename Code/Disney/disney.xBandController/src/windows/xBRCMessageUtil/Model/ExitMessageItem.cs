using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;

namespace xBRCMessageUtil
{
    [XmlRootAttribute(ElementName = "message", IsNullable = false)]
    public class ExitMessageItem : LoadMessageItem
    {
        [XmlElement("totaltime")]
        public int TotalTime { get; set; }
    }
}
