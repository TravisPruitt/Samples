using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.xBMS
{
    public class CustomizationSelection : ModelBase<CustomizationSelection>
    {
        private String xbandOwnerId;
        private String xbandRequestId;
        private String guestId;
        private String guestIdType;
        private List<String> entitlements;
        private String customizationSelectionId;
        private List<QualifyingId> qualifyingIds;
        private String bandProductCode;
        private String printedName;
        private Boolean confirmedCustomization;
        private String firstName;
        private Boolean primaryGuest;
        private String createDate;
        private String lastName;
        private String updateDate;
        private String xband;
        private String self;

        public String XbandOwnerId
        {
            get { return this.xbandOwnerId; }
            set
            {
                this.xbandOwnerId = value;
                NotifyPropertyChanged(m => m.XbandOwnerId);

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

        public String GuestId
        {
            get { return this.guestId; }
            set
            {
                this.guestId = value;
                NotifyPropertyChanged(m => m.GuestId);

            }
        }

        public String GuestIdType
        {
            get { return this.guestIdType; }
            set
            {
                this.guestIdType = value;
                NotifyPropertyChanged(m => m.GuestIdType);

            }
        }

        public List<String> Entitlements
        {
            get { return this.entitlements; }
            set
            {
                this.entitlements = value;
                NotifyPropertyChanged(m => m.Entitlements);

            }
        }

        public String CustomizationSelectionId
        {
            get { return this.customizationSelectionId; }
            set
            {
                this.customizationSelectionId = value;
                NotifyPropertyChanged(m => m.CustomizationSelectionId);

            }
        }

        public List<QualifyingId> QualifyingIds
        {
            get { return this.qualifyingIds; }
            set
            {
                this.qualifyingIds = value;
                NotifyPropertyChanged(m => m.QualifyingIds);

            }
        }

        public String BandProductCode
        {
            get { return this.bandProductCode; }
            set
            {
                this.bandProductCode = value;
                NotifyPropertyChanged(m => m.BandProductCode);

            }
        }

        public String PrintedName
        {
            get { return this.printedName; }
            set
            {
                this.printedName = value;
                NotifyPropertyChanged(m => m.PrintedName);

            }
        }

        public Boolean ConfirmedCustomization
        {
            get { return this.confirmedCustomization; }
            set
            {
                this.confirmedCustomization = value;
                NotifyPropertyChanged(m => m.ConfirmedCustomization);

            }
        }

        public Boolean PrimaryGuest
        {
            get { return this.primaryGuest; }
            set
            {
                this.primaryGuest = value;
                NotifyPropertyChanged(m => m.PrimaryGuest);

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

        public String FirstName
        {
            get { return this.firstName; }
            set
            {
                this.firstName = value;
                NotifyPropertyChanged(m => m.FirstName);

            }
        }

        public String LastName
        {
            get { return this.lastName; }
            set
            {
                this.lastName = value;
                NotifyPropertyChanged(m => m.LastName);

            }
        }

        public String Xband
        {
            get { return this.xband; }
            set
            {
                this.xband = value;
                NotifyPropertyChanged(m => m.Xband);

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
