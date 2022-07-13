using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.GxP
{
    /*
     { 
     * "guestId": 1016,   
      "bookingWindow":    
     * {      
     * "endTime": "2012-05-15T00:00:00-04:00",      
     * "startTime": "2012-03-16T00:00:00-04:00"   
     * },   
     * "eligibilityResult":    
     * [            
     * {         
     * "eligiblePark": false,         
     * "messages": ["7002"],
     * "parkId": 80007998
     * }, 
     * {         
     * "eligiblePark": true,         
     * "messages": ["7007"],         
     * "parkId": 80007944
     * },           
     * {         
     * "eligiblePark": true,      
     * "messages": ["7007"],      
     * "parkId": 80007823      
     * },        
     * {        
     * "eligiblePark": true,  
     * "messages": ["7007"],      
     * "parkId": 80007838   
     * }
     * ]}
     */

    public class IndividualEligibility : ModelBase<IndividualEligibility>
    {
        private string guestId;
        private String firstName;
        private String lastName;
        private BookingWindow bookingWindow;
        private List<EligibilityResult> eligibilityResults;

        public String GuestId
        {
            get { return this.guestId; }
            set
            {
                this.guestId = value;
                NotifyPropertyChanged(m => m.GuestId);

            }
        }

        public String FirstName
        {
            get { return this.firstName; }
            set
            {
                this.firstName = value;
                NotifyPropertyChanged(m => m.FirstName);

            }
        }

        public String LastName
        {
            get { return this.lastName; }
            set
            {
                this.lastName = value;
                NotifyPropertyChanged(m => m.LastName);

            }
        }

        public BookingWindow BookingWindow
        {
            get { return this.bookingWindow; }
            set
            {
                this.bookingWindow = value;
                NotifyPropertyChanged(m => m.BookingWindow);

            }
        }

        public List<EligibilityResult> EligibilityResults
        {
            get { return this.eligibilityResults; }
            set
            {
                this.eligibilityResults = value;
                NotifyPropertyChanged(m => m.EligibilityResults);

            }
        }
    }
}
