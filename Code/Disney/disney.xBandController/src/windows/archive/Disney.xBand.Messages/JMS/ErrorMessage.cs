using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace EM.SonicMQ.Messages.JMS
{
    [XmlRootAttribute(ElementName = "venue", IsNullable = false)]
    public class ErrorMessage : MessageBase
    {
        [XmlElement(ElementName = "message", Type = typeof(ErrorMessageItem))]
        public ErrorMessageItem Message { get; set; }

        public void Save()
        {
            using (EventsEntities context = new EventsEntities())
            {
                context.CreateErrorEvent(this.Message.GuestID, 
                    this.Message.xPass,
                    this.VenueName, this.Message.MessageType, 
                    this.Message.ReaderLocation,
                    this.Message.Timestamp, this.Message.ErrorCode, this.Message.ErrorMessage);
            }
        }
    }
}
