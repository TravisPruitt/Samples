using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel.Activation;
using System.IO;
using System.Web.Script.Serialization;
using System.Runtime.Serialization;
using System.ServiceModel.Web;
using System.ServiceModel;

namespace Disney.xBand.xView
{
    [ServiceContract]
    public interface IxViewService
    {
        [WebGet(UriTemplate = "xbands/{id}", ResponseFormat = WebMessageFormat.Json, BodyStyle = WebMessageBodyStyle.Bare)]
        [OperationContract]
        Stream GetxBand(string id);
        
        [WebGet(UriTemplate = "xbands", ResponseFormat = WebMessageFormat.Json, BodyStyle = WebMessageBodyStyle.Bare)]
        [OperationContract]
        Stream GetxBands();
    }

    // Use a data contract as illustrated in the sample below to add composite types to service operations.
    [DataContract]
    public class xBand
    {
        public string xbandid { get; set; }

        public string lrid { get; set; }

        public string tapid { get; set; }
    }
}
