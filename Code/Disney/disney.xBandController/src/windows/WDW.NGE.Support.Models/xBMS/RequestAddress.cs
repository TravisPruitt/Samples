using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.xBMS
{
    public class RequestAddress : ModelBase<RequestAddress>
    {
        private Address address;
        private Boolean confirmedAddress;
        private String phoneNumber;

        public Address Address
        {
            get { return this.address; }
            set
            {
                this.address = value;
                NotifyPropertyChanged(m => m.Address);

            }
        }

        public Boolean ConfirmedAddress
        {
            get { return this.confirmedAddress; }
            set
            {
                this.confirmedAddress = value;
                NotifyPropertyChanged(m => m.ConfirmedAddress);

            }
        }

        public String PhoneNumber
        {
            get { return this.phoneNumber; }
            set
            {
                this.phoneNumber = value;
                NotifyPropertyChanged(m => m.PhoneNumber);

            }
        }
    }
}
