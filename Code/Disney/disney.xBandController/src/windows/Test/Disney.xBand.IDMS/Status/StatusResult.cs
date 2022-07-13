using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;
using System.Xml.Serialization;

namespace Disney.xBand.IDMS.Status
{

    /* <status>
  <databaseVersion>1.2.0.0001</databaseVersion> 
  <hostname>nl-flfa-00073</hostname> 
  <startTime>2012-07-11T09:14:58.540-04:00</startTime> 
  <status>Green</status> 
  <statusMessage /> 
  <version>1.0.0.1826</version> 
  </status
     */
    [XmlRootAttribute("status")]
    public class StatusResult
    {
        [XmlElement("databaseVersion")]
        public string DatabaseVersion { get; set; }

        [XmlElement("hostname")]
        public string Hostname { get; set; }

        [XmlElement("startTime")]
        public string StartTime { get; set; }

        [XmlElement("status")]
        public string Status { get; set; }

        [XmlElement("statusMessage")]
        public string StatusMessage { get; set; }

        [XmlElement("version")]
        public string Version { get; set; }
    }
}
