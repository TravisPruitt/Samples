using System;
using System.Windows;
using System.Threading;
using System.Collections.ObjectModel;

// Toolkit namespace
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.GXP.ViewModels
{
    /// <summary>
    /// This class contains properties that a View can data bind to.
    /// <para>
    /// Use the <strong>mvvmprop</strong> snippet to add bindable properties to this ViewModel.
    /// </para>
    /// </summary>
    public class IndividualEligibilityViewModel : ViewModelDetailBase<IndividualEligibilityViewModel, Models.GxP.IndividualEligibility>
    {
        private Models.Services.IGxPServiceAgent serviceAgent;
        private string xid;
        private string date;

        public string XID
        {
            get { return this.xid; }
            set
            {
                this.xid = value;
                NotifyPropertyChanged(m => m.XID);

                // Raise can execute changed event on command
                CheckIndvidualEligiblityCommand.RaiseCanExecuteChanged();
            }
        }

        public string Date
        {
            get { return this.date; }
            set
            {
                this.date = value;
                NotifyPropertyChanged(m => m.Date);

                // Raise can execute changed event on command
                CheckIndvidualEligiblityCommand.RaiseCanExecuteChanged();
            }
        }        
        
        // Default ctor
        public IndividualEligibilityViewModel() 
        {
            //TODO: Default to production???
            this.serviceAgent = new Models.Services.GxPServiceAgent();
        }

        public IndividualEligibilityViewModel(Models.Services.IGxPServiceAgent serviceAgent)
        {
            this.serviceAgent = serviceAgent;
        }

        public event EventHandler<NotificationEventArgs<Exception>> ErrorNotice;

        public void CheckIndividualEligibility()
        {
            base.Model = serviceAgent.CheckEligibility(this.date, this.xid);
        }

        private bool CanCheckIndividualEligiblity()
        {
            return !String.IsNullOrEmpty(this.date) && !String.IsNullOrEmpty(this.xid);
        }

        private DelegateCommand checkIndvidualEligiblityCommand;
        public DelegateCommand CheckIndvidualEligiblityCommand
        {
            get { return checkIndvidualEligiblityCommand ?? (checkIndvidualEligiblityCommand = new DelegateCommand(CheckIndividualEligibility, CanCheckIndividualEligiblity)); }
            private set { checkIndvidualEligiblityCommand = value; }
        }
        
        // Helper method to notify View of an error
        private void NotifyError(string message, Exception error)
        {
            // Notify view of an error
            Notify(ErrorNotice, new NotificationEventArgs<Exception>(message, error));
        }
    }
}