using System;
using System.Windows;
using System.Threading;
using System.Collections.ObjectModel;

// Toolkit namespace
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.GXP.ViewModels
{
    /// <summary>
    /// This class extends ViewModelDetailBase which implements IEditableDataObject.
    /// <para>
    /// Specify type being edited <strong>DetailType</strong> as the second type argument
    /// and as a parameter to the seccond ctor.
    /// </para>
    /// <para>
    /// Use the <strong>mvvmprop</strong> snippet to add bindable properties to this ViewModel.
    /// </para>
    /// </summary>
    public class GuestEligibilityViewModel : ViewModelDetailBase<GuestEligibilityViewModel, Models.OneView.GuestProfile>
    {
        private Models.Services.IOneViewServiceAgent serviceAgent;
        private Models.Services.IGxPServiceAgent gxpServiceAgent;

        private string visualId;
        private string xid;
        private DateTime date;
        private bool isBusy;

        public IndividualEligibilityViewModel IndividualEligibilityViewModel { get; private set; }

        public string VisualId
        {
            get { return this.visualId; }
            set
            {
                this.visualId = value;
                if (!String.IsNullOrEmpty(value))
                {
                    this.XID = String.Empty;
                }
                NotifyPropertyChanged(m => m.VisualId);

                // Raise can execute changed event on command
                CheckGuestEligibilityCommand.RaiseCanExecuteChanged();
            }
        }


        public string XID
        {
            get { return this.xid; }
            set
            {
                this.xid = value;
                if (!String.IsNullOrEmpty(value))
                {
                    this.VisualId = String.Empty;
                }
                NotifyPropertyChanged(m => m.XID);

                // Raise can execute changed event on command
                CheckGuestEligibilityCommand.RaiseCanExecuteChanged();
            }
        }

        public DateTime Date
        {
            get { return this.date; }
            set
            {
                this.date = value;
                NotifyPropertyChanged(m => m.Date);

                // Raise can execute changed event on command
                CheckGuestEligibilityCommand.RaiseCanExecuteChanged();
            }
        }

        public bool IsBusy
        {
            get { return this.isBusy; }
            set
            {
                this.isBusy = value;
                NotifyPropertyChanged(m => m.IsBusy);
            }
        }
        // Default ctor
        public GuestEligibilityViewModel()
            : this(new Models.Services.OneViewServiceAgent())
        { 
        }

        public GuestEligibilityViewModel(Models.OneView.GuestProfile model)
        {
            base.Model = model;
        }

        public GuestEligibilityViewModel(Models.Services.IOneViewServiceAgent serviceAgent)
        {
            this.serviceAgent = serviceAgent;
            this.gxpServiceAgent = new Models.Services.GxPServiceAgent();
            this.IndividualEligibilityViewModel = new IndividualEligibilityViewModel();
            this.date = DateTime.Now;
            this.isBusy = false;
        }

        public event EventHandler<NotificationEventArgs<Exception>> ErrorNotice;

        public void CheckGuestEligibility()
        {
            this.IsBusy = true;

            try
            {
                if (!String.IsNullOrEmpty(this.xid))
                {
                    base.Model = serviceAgent.GetGuestProfile("xid", this.xid);
                    this.IndividualEligibilityViewModel.Date = this.date.ToString("yyyy-MM-dd");
                    this.IndividualEligibilityViewModel.XID = this.xid;
                    this.IndividualEligibilityViewModel.CheckIndividualEligibility();
                }
                else
                {
                    base.Model = serviceAgent.GetGuestProfile("xband-external-number", this.visualId);

                    this.IndividualEligibilityViewModel.Date = this.date.ToString("yyyy-MM-dd");
                    this.IndividualEligibilityViewModel.XID = base.Model.XID;
                    this.IndividualEligibilityViewModel.CheckIndividualEligibility();
                }
            }
            finally
            {
                this.IsBusy = false;
            }
        }

        private bool CanCheckGuestEligibility()
        {
            return this.date!=null && !(String.IsNullOrEmpty(this.xid) || String.IsNullOrEmpty(this.visualId));
        }

        private DelegateCommand checkGuestEligibilityCommand;
        public DelegateCommand CheckGuestEligibilityCommand
        {
            get { return checkGuestEligibilityCommand ?? (checkGuestEligibilityCommand = new DelegateCommand(CheckGuestEligibility, CanCheckGuestEligibility)); }
            private set { checkGuestEligibilityCommand = value; }
        }
        
        // Helper method to notify View of an error
        private void NotifyError(string message, Exception error)
        {
            // Notify view of an error
            Notify(ErrorNotice, new NotificationEventArgs<Exception>(message, error));
        }
    }
}