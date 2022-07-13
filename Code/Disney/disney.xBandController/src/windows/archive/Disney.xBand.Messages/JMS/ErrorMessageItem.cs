using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace EM.SonicMQ.Messages.JMS
{
    public class ErrorMessageItem : SimpleMessageItem
    {
        [XmlElement("errorcode")]
        public string ErrorCode { get; set; }

        [XmlElement("errormessage")]
        public string ErrorMessage { get; set; }
    }
}
