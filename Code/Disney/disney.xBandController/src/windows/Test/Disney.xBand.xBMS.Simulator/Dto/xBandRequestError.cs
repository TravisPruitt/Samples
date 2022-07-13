using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.xBMS.Simulator.Dto
{
    [DataContract]
    public class xBandRequestError
    {
        [DataMember(Name = "application", Order = 1)]
        public String Application { get; set; }

        [DataMember(Name = "code", Order = 2)]
        public String Code { get; set; }

        [DataMember(Name = "message", Order = 3)]
        public String Message { get; set; }

        [DataMember(Name = "self", Order = 4)]
        public String Self { get; set; }

        [DataMember(Name = "X-Correlation-ID", Order = 5)]
        public String xCorrelationId { get; set; }

        [DataMember(Name = "X-System-ID", Order = 6)]
        public String xSystemId { get; set; }

        [DataMember(Name = "X-Guest-ID", Order = 7)]
        public String xGuestId { get; set; }

        [DataMember(Name = "X-CAST-ID", Order = 8)]
        public String xCastId { get; set; }

        [DataMember(Name = "X-CIP", Order = 9)]
        public String xCip { get; set; }

        [DataMember(Name = "X-Origin-System-ID", Order = 10)]
        public String xOriginSystemId { get; set; }

    }
}
