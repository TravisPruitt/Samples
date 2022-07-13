using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.GXP.ViewModels
{
    public class XbandRequestDetailsViewModel : ViewModelDetailBase<XbandRequestDetailsViewModel, Models.xBMS.XbandRequestDetails>
    {
        // Add a member for ICustomerServiceAgent
        private Models.Services.IXBMSServiceAgent serviceAgent;
        private string xbandRequestId;
        private string travelPlanId;
       
        // Default ctor
        public XbandRequestDetailsViewModel()
            : this(new Models.Services.XBMSServiceAgent("http://nge-prod-xbms.wdw.disney.com:8080/xbms-rest/app/"))
        { 
        }


        // Ctor that accepts ICustomerServiceAgent
        public XbandRequestDetailsViewModel(Models.Services.IXBMSServiceAgent serviceAgent)
        {
            this.serviceAgent = serviceAgent;
        }

        // TODO: Add events to notify the view or obtain data from the view
        public event EventHandler<NotificationEventArgs<Exception>> ErrorNotice;
        // TODO: Add properties using the mvvmprop code snippet

        public string XbandRequestId
        {
            get { return this.xbandRequestId; }
            set
            {
                this.xbandRequestId = value;
                this.travelPlanId = String.Empty;
                NotifyPropertyChanged(m => m.XbandRequestId);

                // Raise can execute changed event on command
                GetXbandRequestDetailsCommand.RaiseCanExecuteChanged();
            }
        }

        public string TravelPlanId
        {
            get { return this.travelPlanId; }
            set
            {
                this.travelPlanId = value;
                this.xbandRequestId = String.Empty;
                NotifyPropertyChanged(m => m.TravelPlanId);

                // Raise can execute changed event on command
                GetXbandRequestDetailsCommand.RaiseCanExecuteChanged();
            }
        }
        
        public void GetXbandRequestDetails()
        {
            if (!String.IsNullOrEmpty(this.travelPlanId))
            {
                Models.Services.ServiceResult<Models.xBMS.XbandRequestDetails> serviceResult = 
                    serviceAgent.GetXbandRequestDetailsByTravelPlan(this.travelPlanId);

                if (serviceResult.Status == Models.Services.ServiceCallStatus.OK)
                {
                    base.Model = serviceResult.Result;
                }
            }
            else if (!String.IsNullOrEmpty(this.xbandRequestId))
            {
                Models.Services.ServiceResult<Models.xBMS.XbandRequestDetails> serviceResult =
                    serviceAgent.GetXbandRequestDetailsByTravelPlan(this.xbandRequestId);

                if (serviceResult.Status == Models.Services.ServiceCallStatus.OK)
                {
                    base.Model = serviceResult.Result;
                }
            }
        }

        private bool CanGetXbandRequestDetails()
        {
            return !String.IsNullOrEmpty(this.xbandRequestId) || !String.IsNullOrEmpty(this.travelPlanId);
        }

        private DelegateCommand getXbandRequestDetailsCommand;
        public DelegateCommand GetXbandRequestDetailsCommand
        {
            get { return getXbandRequestDetailsCommand ??
                (getXbandRequestDetailsCommand = new DelegateCommand(GetXbandRequestDetails, CanGetXbandRequestDetails));
            }
            private set { getXbandRequestDetailsCommand = value; }
        }

        // Helper method to notify View of an error
        private void NotifyError(string message, Exception error)
        {
            // Notify view of an error
            Notify(ErrorNotice, new NotificationEventArgs<Exception>(message, error));
        }
    }
}
