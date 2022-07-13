using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using Disney.xBand.Data;

namespace Disney.xBand.Messages.JMS
{
    public class AbandonMessage : MessageBase
    {
        [XmlElement(ElementName = "message", Type = typeof(AbandonMessageItem))]
        public AbandonMessageItem Message { get; set; }

        public void Save()
        {
            using (EventsEntities context = new EventsEntities())
            {
                context.CreateAbandonEvent(this.Message.GuestID, this.Message.xPass, 
                    this.FacilityName, this.FacilityTypeName, this.Message.MessageType, this.Message.ReaderLocation,
                    this.Message.Timestamp, this.Message.LastTransmit);
            }
        }

    }
}
