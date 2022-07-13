using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.OneView
{
    public class GuestEligibility : ModelBase<GuestEligibility>
    {
        private String magicPlusParticipantStatus;
        private String magicPlusParticipantStatusEffectiveDate;
        private GuestEligibilityLinks links;


        public String MagicPlusParticipantStatus
        {
            get { return this.magicPlusParticipantStatus; }
            set
            {
                this.magicPlusParticipantStatus = value;
                NotifyPropertyChanged(m => m.MagicPlusParticipantStatus);

            }
        }

        public String MagicPlusParticipantStatusEffectiveDate
        {
            get { return this.magicPlusParticipantStatusEffectiveDate; }
            set
            {
                this.magicPlusParticipantStatusEffectiveDate = value;
                NotifyPropertyChanged(m => m.MagicPlusParticipantStatusEffectiveDate);

            }
        }

        public GuestEligibilityLinks Links
        {
            get { return this.links; }
            set
            {
                this.links = value;
                NotifyPropertyChanged(m => m.Links);

            }
        }

    }
}
