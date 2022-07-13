using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.xBMS
{
    /*{

    "state": "ACTIVE",
    "options": "/xband-options/6DE37139-F959-4DB3-A9D9-DE615FEBCEC9",
    "publicId": 8199612,
    "productId": "C00300",
    "xbandOwnerId": "07274979-CC06-4B95-9BE7-ADA1BBD3973B",
    "secondaryState": "ORIGINAL",
    "externalNumber": "133362250334",
    "secureId": 1008597917268866,
    "shortRangeTag": 36076674938865410,
    "guestId": "31559998",
    "guestIdType": "transactional-guest-id",
    "printedName": "MCKENNA",
    "xbandId": "6DE37139-F959-4DB3-A9D9-DE615FEBCEC9",
    "xbandRequest": "/xband-requests/1E26EFCC-9765-491B-84D5-757B94317621",
    "assignmentDateTime": "2012-11-16T02:56:25Z",
    "history": "/xband-history/6DE37139-F959-4DB3-A9D9-DE615FEBCEC9",
    "self": "/xband/6DE37139-F959-4DB3-A9D9-DE615FEBCEC9"
}*/

    public class XbandDetails : ModelBase<XbandDetails>
    {
        private String state { get; set; }
        private String options { get; set; }
        private long publicId { get; set; }
        private String productId { get; set; }
        private String xbandOwnerId { get; set; }
        private String secondaryState { get; set; }
        private String externalNumber { get; set; }
        private long secureId { get; set; }
        private long shortRangeTag { get; set; }
        private String guestId { get; set; }
        private String guestIdType { get; set; }
        private String printedName { get; set; }
        private String xbandId { get; set; }
        private String xbandRequest { get; set; }
        private String assignmentDateTime { get; set; }
        private String history { get; set; }
        private String bandRole { get; set; }
        private String self { get; set; }

        public String State
        {
            get { return this.state; }
            set
            {
                this.state = value;
                NotifyPropertyChanged(m => m.State);

            }
        }

        public String Options
        {
            get { return this.options; }
            set
            {
                this.options = value;
                NotifyPropertyChanged(m => m.Options);

            }
        }

        public long PublicId
        {
            get { return this.publicId; }
            set
            {
                this.publicId = value;
                NotifyPropertyChanged(m => m.PublicId);

            }
        }

        public String ProductId
        {
            get { return this.productId; }
            set
            {
                this.productId = value;
                NotifyPropertyChanged(m => m.ProductId);

            }
        }

        public String XbandOwnerId
        {
            get { return this.xbandOwnerId; }
            set
            {
                this.xbandOwnerId = value;
                NotifyPropertyChanged(m => m.XbandOwnerId);

            }
        }

        public String SecondaryState
        {
            get { return this.secondaryState; }
            set
            {
                this.secondaryState = value;
                NotifyPropertyChanged(m => m.SecondaryState);

            }
        }

        public String ExternalNumber
        {
            get { return this.externalNumber; }
            set
            {
                this.externalNumber = value;
                NotifyPropertyChanged(m => m.ExternalNumber);

            }
        }

        public long SecureId
        {
            get { return this.secureId; }
            set
            {
                this.secureId = value;
                NotifyPropertyChanged(m => m.SecureId);

            }
        }

        public long ShortRangeTag
        {
            get { return this.shortRangeTag; }
            set
            {
                this.shortRangeTag = value;
                NotifyPropertyChanged(m => m.ShortRangeTag);

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

        public String PrintedName
        {
            get { return this.printedName; }
            set
            {
                this.printedName = value;
                NotifyPropertyChanged(m => m.PrintedName);

            }
        }

        public String XbandId
        {
            get { return this.xbandId; }
            set
            {
                this.xbandId = value;
                NotifyPropertyChanged(m => m.XbandId);

            }
        }

        public String XbandRequest
        {
            get { return this.xbandRequest; }
            set
            {
                this.xbandRequest = value;
                NotifyPropertyChanged(m => m.XbandRequest);

            }
        }

        public String AssignmentDateTime
        {
            get { return this.assignmentDateTime; }
            set
            {
                this.assignmentDateTime = value;
                NotifyPropertyChanged(m => m.AssignmentDateTime);

            }
        }

        public String History
        {
            get { return this.history; }
            set
            {
                this.history = value;
                NotifyPropertyChanged(m => m.History);

            }
        }

        public String BandRole
        {
            get { return this.bandRole; }
            set
            {
                this.bandRole = value;
                NotifyPropertyChanged(m => m.BandRole);

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
