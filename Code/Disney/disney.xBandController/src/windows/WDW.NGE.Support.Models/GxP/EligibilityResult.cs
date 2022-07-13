using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.GxP
{
    public class EligibilityResult : ModelBase<EligibilityResult>
    {
        private Boolean eligiblePark;
        private List<String> messages;
        private long parkId;

        public Boolean EligiblePark
        {
            get { return this.eligiblePark; }
            set
            {
                this.eligiblePark = value;
                NotifyPropertyChanged(m => m.EligiblePark);

            }
        }

        public List<String> Messages
        {
            get { return this.messages; }
            set
            {
                this.messages = value;
                NotifyPropertyChanged(m => m.Messages);

            }
        }

        public long ParkId
        {
            get { return this.parkId; }
            set
            {
                this.parkId = value;
                NotifyPropertyChanged(m => m.ParkId);

            }
        }

        public List<String> MessageResults
        {
            get
            {
                //Shouldn't happen, but just in case.
                if (this.Messages == null)
                {
                    return null;
                }
                else
                {
                    //Create result
                    List<String> result = new List<String>();

                    //Convert each message to a human readable value. 
                    //If more message codes are added update the MessageCode enumeration.
                    foreach (String message in this.Messages)
                    {
                        MessageCode messageCode = (MessageCode) Enum.Parse(typeof(MessageCode),message);
                        result.Add(messageCode.GetDescription());
                    }

                    return result;
                }
            }
        }
    }
}
