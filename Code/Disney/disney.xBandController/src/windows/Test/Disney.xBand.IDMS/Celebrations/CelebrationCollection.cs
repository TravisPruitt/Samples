using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;
using Disney.xBand.IDMS.Links;

namespace Disney.xBand.IDMS.Celebrations
{
    [DataContract]
    public class CelebrationCollection
    {
        [DataMember(Name="links")]
        public LinkCollection Links { get; set; }


        [DataMember(Name = "magicalCelebrations")]
        public List<Celebration> Celebrations { get; set; }

    }
}
