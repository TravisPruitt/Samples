using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models
{
    public class CreateBandStatusItem : ModelBase<CreateBandStatusItem>
    {
        private String xbandId;
        public String XbandId
        {
            get { return xbandId; }
            set
            {
                xbandId = value;
                NotifyPropertyChanged(m => m.XbandId);
            }
        }

        private CreateBandStatus status;
        public CreateBandStatus Status
        {
            get { return status; }
            set
            {
                status = value;
                NotifyPropertyChanged(m => m.Status);
            }
        }
    }
}
