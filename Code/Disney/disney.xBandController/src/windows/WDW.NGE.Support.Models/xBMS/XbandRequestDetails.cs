using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.xBMS
{
    public class XbandRequestDetails  : ModelBase<XbandRequestDetails>
    {
        public String options;
        public String order;
        public String state;
        public String primaryGuestOwnerId;
        public String xbandRequestId;
        public String acquisitionId;
        public String acquisitionIdType;
        public String acquisitionStartDate;
        public List<ResortReservation> resortReservations;
        public RequestAddress requestAddress;
        public Shipment shipment;
        public List<CustomizationSelection> customizationSelections;
        public String acquisitionUpdateDate;
        public String createDate;
        public String updateDate;
        public String reorder;
        public String customizationEndDate;
        public String self;

        public String Options
        {
            get { return this.options; }
            set
            {
                this.options = value;
                NotifyPropertyChanged(m => m.Options);

            }
        }

        public String Order
        {
            get { return this.order; }
            set
            {
                this.order = value;
                NotifyPropertyChanged(m => m.Order);

            }
        }

        public String State
        {
            get { return this.state; }
            set
            {
                this.state = value;
                NotifyPropertyChanged(m => m.State);

            }
        }

        public String PrimaryGuestOwnerId
        {
            get { return this.primaryGuestOwnerId; }
            set
            {
                this.primaryGuestOwnerId = value;
                NotifyPropertyChanged(m => m.PrimaryGuestOwnerId);

            }
        }

        public String XbandRequestId
        {
            get { return this.xbandRequestId; }
            set
            {
                this.xbandRequestId = value;
                NotifyPropertyChanged(m => m.XbandRequestId);

            }
        }

        public String AcquisitionId
        {
            get { return this.acquisitionId; }
            set
            {
                this.acquisitionId = value;
                NotifyPropertyChanged(m => m.AcquisitionId);

            }
        }

        public String AcquisitionIdType
        {
            get { return this.acquisitionIdType; }
            set
            {
                this.acquisitionIdType = value;
                NotifyPropertyChanged(m => m.AcquisitionIdType);

            }
        }

        public String AcquisitionStartDate
        {
            get { return this.acquisitionStartDate; }
            set
            {
                this.acquisitionStartDate = value;
                NotifyPropertyChanged(m => m.AcquisitionStartDate);

            }
        }

        public List<ResortReservation> ResortReservations
        {
            get { return this.resortReservations; }
            set
            {
                this.resortReservations = value;
                NotifyPropertyChanged(m => m.ResortReservations);

            }
        }

        public RequestAddress RequestAddress
        {
            get { return this.requestAddress; }
            set
            {
                this.requestAddress = value;
                NotifyPropertyChanged(m => m.RequestAddress);

            }
        }

        public Shipment Shipment
        {
            get { return this.shipment; }
            set
            {
                this.shipment = value;
                NotifyPropertyChanged(m => m.Shipment);

            }
        }

        public List<CustomizationSelection> CustomizationSelections
        {
            get { return this.customizationSelections; }
            set
            {
                this.customizationSelections = value;
                NotifyPropertyChanged(m => m.CustomizationSelections);

            }
        }

        public String AcquisitionUpdateDate
        {
            get { return this.acquisitionUpdateDate; }
            set
            {
                this.acquisitionUpdateDate = value;
                NotifyPropertyChanged(m => m.AcquisitionUpdateDate);

            }
        }

        public String CreateDate
        {
            get { return this.createDate; }
            set
            {
                this.createDate = value;
                NotifyPropertyChanged(m => m.CreateDate);

            }
        }
        public String UpdateDate
        {
            get { return this.updateDate; }
            set
            {
                this.updateDate = value;
                NotifyPropertyChanged(m => m.UpdateDate);

            }
        }

        public String Reorder
        {
            get { return this.reorder; }
            set
            {
                this.reorder = value;
                NotifyPropertyChanged(m => m.Reorder);

            }
        }

        public String CustomizationEndDate
        {
            get { return this.customizationEndDate; }
            set
            {
                this.customizationEndDate = value;
                NotifyPropertyChanged(m => m.CustomizationEndDate);

            }
        }

        public String Self
        {
            get { return this.self; }
            set
            {
                this.self = value;
                NotifyPropertyChanged(m => m.Self);

            }
        }
    }
}
