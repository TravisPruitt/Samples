using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using Disney.xBand.Data;

namespace Disney.xBand.Messages.JMS
{
    [XmlRootAttribute(ElementName = "venue", IsNullable = false)]
    public class LoadMessage : MessageBase
    {
        [XmlElement(ElementName = "message", Type = typeof(LoadMessageItem))]
        public LoadMessageItem Message { get; set; }

        public void Save()
        {
            using (EventsEntities context = new EventsEntities())
            {
                 context.CreateLoadEvent(this.Message.GuestID, 
                     this.Message.xPass,
                     this.FacilityName, 
                     this.FacilityTypeName,
                     this.Message.MessageType, this.Message.ReaderLocation,
                     this.Message.Timestamp, this.Message.WaitTime, this.Message.MergeTime, this.Message.CarID);
            }
        }

    }
}
