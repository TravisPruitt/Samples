using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.Common
{
    public class GuestIdentifierResult : ModelBase<GuestIdentifierResult>
    {
        private List<Common.GuestIdentifier> identifiers;
        public List<Common.GuestIdentifier> Identifiers
        {
            get
            {
                return this.identifiers;
            }
            set
            {
                this.identifiers = value;
                NotifyPropertyChanged(m => m.Identifiers);
            }
        }
    }
}
