using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.xBMS
{
    public class Address : ModelBase<Address>
    {
        private string state;
        private string country;
        private string address1;
        private string address2;
        private string city;
        private string postalCode;

        public String State
        {
            get { return this.state; }
            set
            {
                this.state = value;
                NotifyPropertyChanged(m => m.State);

            }
        }

        public String Country
        {
            get { return this.country; }
            set
            {
                this.country = value;
                NotifyPropertyChanged(m => m.Country);

            }
        }

        public String Address1
        {
            get { return this.address1; }
            set
            {
                this.address1 = value;
                NotifyPropertyChanged(m => m.Address1);

            }
        }

        public String Address2
        {
            get { return this.address2; }
            set
            {
                this.address2 = value;
                NotifyPropertyChanged(m => m.Address2);

            }
        }

        public String City
        {
            get { return this.city; }
            set
            {
                this.city = value;
                NotifyPropertyChanged(m => m.City);

            }
        }

        public String PostalCode
        {
            get { return this.postalCode; }
            set
            {
                this.postalCode = value;
                NotifyPropertyChanged(m => m.PostalCode);

            }
        }
    }
}
