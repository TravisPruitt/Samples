using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;

namespace xBRCMessageUtil.Model.Model
{
    public class LocationInfo
    {
        [XmlElement("name")]
        public string Name { get; set; }

        [XmlElement("arrived")]
        public string Arrived { get; set; }
    }
}
