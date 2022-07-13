using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using Disney.xBand.Data;

namespace Disney.xBand.Messages.JMS
{
    [XmlRootAttribute(ElementName = "venue", IsNullable = false)]
    public class MetricsMessage : MessageBase
    {
        [XmlElement("message")]
        public MetricsMessageItem Message { get; set; } 

        public void Save()
        {
            using (EventsEntities context = new EventsEntities())
            {
                context.CreateMetric(this.FacilityName, this.FacilityTypeName, this.Message.StartTime, this.Message.EndTime, "Stand By",
                    this.Message.StandbyMetrics.Guests, this.Message.StandbyMetrics.Abandonments,
                    this.Message.StandbyMetrics.WaitTime, 0, this.Message.StandbyMetrics.TotalTime);

                context.CreateMetric(this.FacilityName, this.FacilityTypeName, this.Message.StartTime, this.Message.EndTime, "xPass",
                    this.Message.xPassMetrics.Guests, this.Message.xPassMetrics.Abandonments,
                    this.Message.xPassMetrics.WaitTime, this.Message.xPassMetrics.MergeTime, this.Message.xPassMetrics.TotalTime);
            }
        }
    }
}
