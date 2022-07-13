using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.OneView
{
    public class GuestEligibilityLinks : ModelBase<GuestEligibilityLinks>
    {
        private Common.Link magicPlusParticipantStatusEvaluationRecords;

        public Common.Link MagicPlusParticipantStatusEvaluationRecords
        {
            get { return this.magicPlusParticipantStatusEvaluationRecords; }
            set
            {
                this.magicPlusParticipantStatusEvaluationRecords = value;
                NotifyPropertyChanged(m => m.MagicPlusParticipantStatusEvaluationRecords);

            }
        }
    }
}
