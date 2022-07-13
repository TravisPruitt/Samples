using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.OneView
{
    public class GuestProfile : ModelBase<GuestProfile>, Common.IGuestProfile
    {
        private string lastName;
        private string firstName;
        private string age;
        private string dateOfBirth;
        private List<Common.GuestIdentifier> guestIdentifiers;
        private GuestEligibility guestEligibility;

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

        public String Age
        {
            get { return this.age; }
            set
            {
                this.age = value;
                NotifyPropertyChanged(m => m.Age);

            }
        }

        public String DateOfBirth
        {
            get { return this.dateOfBirth; }
            set
            {
                this.dateOfBirth = value;
                NotifyPropertyChanged(m => m.DateOfBirth);

            }
        }

        public List<Common.GuestIdentifier> GuestIdentifiers
        {
            get 
            {
                if (this.guestIdentifiers != null)
                {
                    return this.guestIdentifiers.Where(g => !g.IdentifierType.StartsWith("xband-") && !g.IdentifierType.StartsWith("guid")
                        && !g.IdentifierType.StartsWith("pernr"))
                        .OrderBy(g => g.IdentifierType).ToList();
                }

                return null;
            }
            set
            {
                this.guestIdentifiers = value;
                NotifyPropertyChanged(m => m.GuestIdentifiers);

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

        public GuestEligibility GuestEligibility
        {
            get { return this.guestEligibility; }
            set
            {
                this.guestEligibility = value;
                NotifyPropertyChanged(m => m.GuestEligibility);

            }
        }
    }
}
