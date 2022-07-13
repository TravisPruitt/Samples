using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace WDW.NGE.Support.Dto.IDMS
{
    [DataContract]
    public class GuestProfile : Common.IGuestProfile
    {
        [DataMember(Name = "guestId")]
        public long GuestId { get; set; }

        [DataMember(Name = "name")]
        public GuestName Name { get; set; }

        [DataMember(Name = "type")]
        public string IdmsType { get; set; }

        [DataMember(Name = "identifiers")]
        public List<Common.GuestIdentifier> GuestIdentifiers { get; set; }
        
        [DataMember(Name = "xbands")]
        public List<Xband> xBands { get; set; }

        [IgnoreDataMember]
        public String LastName
        {
            get
            {
                if (this.Name != null)
                {
                    return this.Name.LastName; 
                }

                return null;
            }
            
            set
            {
                if (this.Name == null)
                {
                    this.Name = new GuestName();
                }

                this.Name.LastName = value;
            }
        }
        
        [IgnoreDataMember]
        public String FirstName
        {
            get
            {
                if (this.Name != null)
                {
                    return this.Name.FirstName;
                }

                return null;
            }

            set
            {
                if (this.Name == null)
                {
                    this.Name = new GuestName();
                }

                this.Name.FirstName = value;
            }
        }
    }
}
