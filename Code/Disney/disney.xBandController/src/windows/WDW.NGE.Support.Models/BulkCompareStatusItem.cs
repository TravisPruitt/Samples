using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models
{
    public class BulkCompareStatusItem : ModelBase<BulkCompareStatusItem>
    {
        private Common.GuestIdentifier guestIdentifier;
        public Common.GuestIdentifier GuestIdentifier
        {
            get { return guestIdentifier; }
            set
            {
                guestIdentifier = value;
                NotifyPropertyChanged(m => m.GuestIdentifier);
            }
        }

        private GuestCompareStatus status;
        public GuestCompareStatus Status
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
