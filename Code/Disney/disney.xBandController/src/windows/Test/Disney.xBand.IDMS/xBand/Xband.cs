using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;
using Disney.xBand.IDMS.Links;

namespace Disney.xBand.IDMS.xBand
{
    [DataContract]
    public class Xband
    {
        [DataMember(Name="xbandId")]
        public long xBandID { get; set; }

        [DataMember(Name = "publicId")]
        public String PublicId { get; set; }

        [DataMember(Name = "xbandRequestId")]
        public String xBandRequestID { get; set; }

        [DataMember(Name = "secureId")]
        public String SecureId { get; set; }

        [DataMember(Name = "shortRangeTag")]
        public String TapID { get; set; }

        [DataMember(Name = "longRangeTag")]
        public String LongRangeID { get; set; }

        [DataMember(Name = "printedName")]
        public String PrintedName { get; set; }

        [DataMember(Name = "state")]
        public String State { get; set; }

        [DataMember(Name = "secondaryState")]
        public String SecondaryState { get; set; }

        [DataMember(Name = "productId")]
        public String ProductId { get; set; }

        [DataMember(Name = "bandId")]
        public String BandId { get; set; }

        [DataMember(Name = "assignmentDateTime")]
        public String AssignmentDateTime { get; set; }

        [DataMember(Name = "links")]
        public List<Link> oneViewLinks;
    }
}
