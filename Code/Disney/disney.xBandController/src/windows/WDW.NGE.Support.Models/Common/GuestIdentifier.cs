using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.Common
{
    public class GuestIdentifier : ModelBase<GuestIdentifier>
    {
        private string identifierValue;
        private string identifierType;

        public string IdentifierValue 
        { 
            get { return this.identifierValue; }
            set
            {
                this.identifierValue = value;
                NotifyPropertyChanged(m => m.IdentifierValue);

            }
        }

        public string IdentifierType
        {
            get { return this.identifierType; }
            set
            {
                this.identifierType = value;
                NotifyPropertyChanged(m => m.IdentifierType);

            }
        }

        private Boolean match;
        public Boolean Match
        {
            get { return match; }
            set
            {
                match = value;
                NotifyPropertyChanged(m => m.Match);
            }
        }
    }
}
