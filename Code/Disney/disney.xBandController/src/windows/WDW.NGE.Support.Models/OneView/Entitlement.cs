using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.OneView
{
    public class Entitlement : ModelBase<Entitlement>
    {
        private String startTime;
        private String endTime;
        private String type;
        private String id;
        private EntitlementLinks links;
        private List<EntitlementGuest> guests;

        public String StartTime
        {
            get 
            {
                if (this.startTime != null)
                {
                    return DateTime.Parse(this.startTime).ToString("yyyy-MM-dd");
                }
                return null;
            }
            set
            {
                this.startTime = value;
                NotifyPropertyChanged(m => m.StartTime);

            }
        }

        public String EndTime
        {
            get 
            {
                if (this.endTime != null)
                {
                    return DateTime.Parse(this.endTime).ToString("yyyy-MM-dd");
                }
                return null;
            }
            set
            {
                this.endTime = value;
                NotifyPropertyChanged(m => m.EndTime);

            }
        }

        public String Type
        {
            get { return this.type; }
            set
            {
                this.type = value;
                NotifyPropertyChanged(m => m.Type);

            }
        }

        public String ID
        {
            get { return this.id; }
            set
            {
                this.id = value;
                NotifyPropertyChanged(m => m.ID);

            }
        }

        public EntitlementLinks Links
        {
            get
            {
                return this.links;
            }
            set
            {
                this.links = value;
                NotifyPropertyChanged(m => m.Links);

            }
        }

        public List<EntitlementGuest> Guests
        {
            get
            {
                return this.guests;
            }
            set
            {
                this.guests = value;
                NotifyPropertyChanged(m => m.Guests);
            }
        }

        public List<GuestProfile> GuestProfiles
        {
            get
            {
                return this.guests.Select(g => g.Profile).ToList();
            }
        }
    }
}
