using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class ReaderType : DtoObjectBase
    {
        private int readerTypeID;
        private string readerTypeName;

        [DataMember(Name = "ReaderTypeId", Order = 1)]
        public int ReaderTypeID
        {
            get { return this.readerTypeID; }
            set
            {
                this.readerTypeID = value;
                OnPropertyChanged("ReaderTypeID");
            }
        }

        [DataMember(Name = "ReaderTypeName", Order = 2)]
        public string ReaderTypeName
        {
            get { return this.readerTypeName; }
            set
            {
                this.readerTypeName = value;
                OnPropertyChanged("ReaderTypeName");
            }
        }

    }
}
