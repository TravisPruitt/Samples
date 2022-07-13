using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Dto.Common
{
    /// <summary>
    ///     Represents a link to retreive additional information about the guest, xBand, etc. 
    /// </summary>
    [DataContract]
    public class Link
    {
        /// <summary>
        ///     Reference to an endpoint within the system returning the data, i.e. OneView, to get further information
        ///     about the guest, xBand, or reference.
        /// </summary>
        [DataMember(Name = "href", Order = 1)]
        public string Href { get; set; }
    }
}
