using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.IDMS
{
    public class GuestLocators : ModelBase<GuestLocators>
    {
        private List<String> guestLocatorList;

        public List<String> GuestLocatorList
        {
            get { return this.guestLocatorList; }
            set
            {
                this.guestLocatorList = value;
                NotifyPropertyChanged(m => m.GuestLocatorList);

            }
        }
    }
}
