using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class ReaderLocationType : DtoObjectBase
    {
        private int readerLocationTypeID;
        private string readerLocationTypeName;

        [DataMember(Name = "ReaderLocationTypeId", Order = 1)]
        public int ReaderLocationTypeID
        {
            get { return this.readerLocationTypeID; }
            set
            {
                this.readerLocationTypeID = value;
                OnPropertyChanged("ReaderLocationTypeID");
            }
        }

        [DataMember(Name = "ReaderLocationTypeName", Order = 2)]
        public string ReaderLocationTypeName
        {
            get { return this.readerLocationTypeName; }
            set
            {
                this.readerLocationTypeName = value;
                OnPropertyChanged("ReaderLocationTypeName");
            }
        }

    }
}
