using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.GxP
{
    public class GroupEligibility : ModelBase<GroupEligibility>
    {
        private List<IndividualEligibility> indviduals;
        private List<EligibilityResult> groupEligibilityResult;

        public List<IndividualEligibility> Individuals
        {
            get { return this.indviduals; }
            set
            {
                this.indviduals = value;
                NotifyPropertyChanged(m => m.Individuals);

            }
        }

        public List<EligibilityResult> GroupEligibilityResult
        {
            get { return this.groupEligibilityResult; }
            set
            {
                this.groupEligibilityResult = value;
                NotifyPropertyChanged(m => m.GroupEligibilityResult);

            }
        }
    }
}
