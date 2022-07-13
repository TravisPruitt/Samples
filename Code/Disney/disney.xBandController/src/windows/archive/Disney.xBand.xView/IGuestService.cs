using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;
using System.ServiceModel.Activation;
using System.IO;
using System.Web.Script.Serialization;

namespace Disney.xBand.xView
{
    [ServiceContract]
    public interface IGuestService
    {

        [WebGet(UriTemplate = "guests/{id}?timeFrame={seconds}", ResponseFormat = WebMessageFormat.Json, BodyStyle=WebMessageBodyStyle.Bare)]
        [OperationContract]
        Stream GetLastKnowLocations(string id, string seconds);

        [WebGet(UriTemplate = "guests", ResponseFormat = WebMessageFormat.Json, BodyStyle = WebMessageBodyStyle.Bare)]
        [OperationContract]
        Stream GetGuests();
    }


    // Use a data contract as illustrated in the sample below to add composite types to service operations.
    [Serializable]
    public class GuestLocation
    {
        public string attractionname { get; set; }

        public string eventtypename { get; set; }

        [ScriptIgnore()]
        public DateTime DbTimestamp { get; set; }

        public string timestamp { get; set; }

        public string readerlocation { get; set; }
    }

    // Use a data contract as illustrated in the sample below to add composite types to service operations.
    [DataContract]
    public class Guest
    {
        public string guestid { get; set; }
    }
}
