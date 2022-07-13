using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.OneView
{
    public class EntitlementGuest : ModelBase<EntitlementGuest>
    {
        private List<String> entitlementRoles;
        private GuestProfile profile;

        public List<String> EntitlementRoles
        {
            get { return this.entitlementRoles; }
            set
            {
                this.entitlementRoles = value;
                NotifyPropertyChanged(m => m.EntitlementRoles);

            }
        }

        public GuestProfile Profile
        {
            get { return this.profile; }
            set
            {
                this.profile = value;
                NotifyPropertyChanged(m => m.Profile);

            }
        }
    }
}
