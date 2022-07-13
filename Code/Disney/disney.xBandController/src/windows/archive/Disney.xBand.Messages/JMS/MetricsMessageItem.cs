using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace Disney.xBand.Messages.JMS
{
    public class MetricsMessageItem
    {
        [XmlAttribute("type")]
        public string MessageType { get; set; }

        [XmlAttribute("time")]
        public string Timestamp { get; set; }

        [XmlElement("starttime")]
        public string StartTime { get; set; }

        [XmlElement("endtime")]
        public string EndTime { get; set; }

        [XmlElement(ElementName = "standby", Type = typeof(StandbyMetricsMessageItem))]
        public StandbyMetricsMessageItem StandbyMetrics { get; set; }

        [XmlElement(ElementName = "xpass", Type = typeof(XPassMetricsMessageItem))]
        public XPassMetricsMessageItem xPassMetrics { get; set; }


    }

    public class StandbyMetricsMessageItem 
    {
        [XmlElement("guests")]
        public int Guests { get; set; }

        [XmlElement("abandonments")]
        public int Abandonments { get; set; }

        [XmlElement("waittime")]
        public int WaitTime { get; set; }

        [XmlElement("totaltime")]
        public int TotalTime { get; set; }

    }

    public class XPassMetricsMessageItem : StandbyMetricsMessageItem
    {
        [XmlElement("mergetime")]
        public int MergeTime { get; set; }
    }
}
