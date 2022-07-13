using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using System.IO;
using System.Xml;
using System.Data.Objects;
using Disney.xBand.Data;

namespace Disney.xBand.Messages.JMS
{

    [XmlRootAttribute(ElementName = "venue", IsNullable = false)]
    public class SimpleMessage : MessageBase
    {
        
        public void Save()
        {
            using (EventsEntities context = new EventsEntities())
            {
                ObjectParameter parameter = new ObjectParameter("EventID", 0);
                
                context.CreateEvent(this.Message.GuestID, 
                    this.Message.xPass,
                    this.FacilityName, 
                    this.FacilityTypeName,
                    this.Message.MessageType,
                    this.Message.ReaderLocation,
                    this.Message.Timestamp, parameter);
            }
        }

        [XmlElement(ElementName = "message", Type=typeof(SimpleMessageItem))]
        public SimpleMessageItem Message { get; set; }
 
     }
}
