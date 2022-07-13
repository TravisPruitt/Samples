using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.xBMS
{
    public class Shipment : ModelBase<Shipment>
    {
        private Address address;
        private String method;
        private String carrier;
        private String carrierLink;
        private String trackingNumber;
        private String shippingDate;

        public Address Address
        {
            get { return this.address; }
            set
            {
                this.address = value;
                NotifyPropertyChanged(m => m.Address);

            }
        }

        public String Method
        {
            get { return this.method; }
            set
            {
                this.method = value;
                NotifyPropertyChanged(m => m.Method);

            }
        }

        public String Carrier
        {
            get { return this.carrier; }
            set
            {
                this.carrier = value;
                NotifyPropertyChanged(m => m.Carrier);

            }
        }

        public String CarrierLink
        {
            get { return this.carrierLink; }
            set
            {
                this.carrierLink = value;
                NotifyPropertyChanged(m => m.CarrierLink);

            }
        }

        public String TrackingNumber
        {
            get { return this.trackingNumber; }
            set
            {
                this.trackingNumber = value;
                NotifyPropertyChanged(m => m.TrackingNumber);

            }
        }

        public String ShippingDate
        {
            get { return this.shippingDate; }
            set
            {
                this.shippingDate = value;
                NotifyPropertyChanged(m => m.ShippingDate);

            }
        }
    }
}
