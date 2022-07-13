using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.xBMS
{
    public class QualifyingId : ModelBase<QualifyingId>
    {
        private String qualifyingIdType;
        private String qualifyingIdValue;

        public String QualifyingIdType
        {
            get { return this.qualifyingIdType; }
            set
            {
                this.qualifyingIdType = value;
                NotifyPropertyChanged(m => m.QualifyingIdType);

            }
        }

        public String QualifyingIdValue
        {
            get { return this.qualifyingIdValue; }
            set
            {
                this.qualifyingIdValue = value;
                NotifyPropertyChanged(m => m.QualifyingIdValue);

            }
        }
    }
}
