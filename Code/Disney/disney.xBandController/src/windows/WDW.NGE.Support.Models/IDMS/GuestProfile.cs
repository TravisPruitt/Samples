using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.IDMS
{
    public class GuestProfile : ModelBase<GuestProfile>, Common.IGuestProfile
    {
        private long guestId;
        private string lastName;
        private string firstName;
        private List<Common.GuestIdentifier> guestIdentifiers;
        private List<Xband> xbands;

        public long GuestId
        {
            get { return this.guestId; }
            set
            {
                this.guestId = value;
                NotifyPropertyChanged(m => m.GuestId);

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

        public String FirstName
        {
            get { return this.firstName; }
            set
            {
                this.firstName = value;
                NotifyPropertyChanged(m => m.FirstName);

            }
        }

        public List<Common.GuestIdentifier> GuestIdentifiers
        {
            get
            {
                if (this.guestIdentifiers != null)
                {
                    return this.guestIdentifiers.OrderBy(g => g.IdentifierType).ToList();
                }
                return null;
            }
            set
            {
                this.guestIdentifiers = value;
                NotifyPropertyChanged(m => m.GuestIdentifiers);

            }
        }

        public List<Xband> xBands
        {
            get { return this.xbands; }
            set
            {
                this.xbands = value;
                NotifyPropertyChanged(m => m.xBands);

            }
        }

        public String XID
        {
            get
            {
                if (this.GuestIdentifiers != null)
                {
                    Common.GuestIdentifier guestIdentifier =
                        this.guestIdentifiers.Where(g => g.IdentifierType == "xid").FirstOrDefault();

                    if (guestIdentifier != null)
                    {
                        return guestIdentifier.IdentifierValue;
                    }
                }

                return null;
            }
        }

        public bool HasMultipleXids
        {
            get
            {
                if (this.GuestIdentifiers != null)
                {
                    int xidCount =
                        this.GuestIdentifiers.Count(g => g.IdentifierType == "xid");

                    if (xidCount > 1)
                    {
                        return true;
                    }
                }

                return false;
            }
        }
    }
}
