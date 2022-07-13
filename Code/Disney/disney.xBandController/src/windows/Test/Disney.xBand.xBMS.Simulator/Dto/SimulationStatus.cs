using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.xBMS.Simulator.Dto
{
    [DataContract]
    public class SimulationStatus
    {
        [DataMember(Name = "xBandRequestMessagesRemaining", Order = 1)]
        public int xBandRequestMessagesRemaining { get; set; }

        [DataMember(Name = "xBandRequestMessagesProcessed", Order = 2)]
        public int xBandRequestMessagesProcessed { get; set; }

        [DataMember(Name = "xBandRequestMessagesValidated", Order = 3)]
        public int xBandRequestMessagesValidated { get; set; }

        [DataMember(Name = "xBandRequestMessageErrors", Order = 4)]
        public int xBandRequestMessageErrors { get; set; }

        [DataMember(Name = "xBandRequestStatus", Order = 5)]
        public String xBandRequestStatus { get; set; }

        [DataMember(Name = "xBandMessagesRemaining", Order = 6)]
        public int xBandMessagesRemaining { get; set; }

        [DataMember(Name = "xBandMessagesProcessed", Order = 7)]
        public int xBandMessagesProcessed { get; set; }

        [DataMember(Name = "xBandMessagesValidated", Order = 8)]
        public int xBandMessagesValidated { get; set; }

        [DataMember(Name = "xBandMessagesErrors", Order = 9)]
        public int xBandMessageErrors { get; set; }

        [DataMember(Name = "xBandStatus", Order = 10)]
        public String xBandStatus { get; set; }
    }
}
