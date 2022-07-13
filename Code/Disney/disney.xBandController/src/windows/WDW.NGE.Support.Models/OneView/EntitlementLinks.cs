using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.OneView
{
    public class EntitlementLinks : ModelBase<EntitlementLinks>
    {
        private Common.Link self;

        public Common.Link Self
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
