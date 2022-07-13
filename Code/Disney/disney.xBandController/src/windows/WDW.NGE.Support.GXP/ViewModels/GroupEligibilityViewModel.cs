using System;
using System.Windows;
using System.Threading;
using System.Collections.ObjectModel;

// Toolkit namespace
using SimpleMvvmToolkit;
using System.Collections.Generic;

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
    public class GroupEligibilityViewModel : ViewModelDetailBase<GroupEligibilityViewModel, Models.GxP.GroupEligibility>
    {
        private Models.Services.GxPServiceAgent serviceAgent;
        private DateTime date;
        private ObservableCollection<Models.OneView.GuestProfile> guestProfiles;

        // Default ctor
        public GroupEligibilityViewModel()
            : this(new Models.Services.GxPServiceAgent())
        {
        }

        public GroupEligibilityViewModel(Models.GxP.GroupEligibility model)
        {
            base.Model = model;
        }

        public GroupEligibilityViewModel(Models.Services.GxPServiceAgent serviceAgent)
        {
            this.serviceAgent = serviceAgent;
            this.guestProfiles = new ObservableCollection<Models.OneView.GuestProfile>();
        }

        // TODO: Add events to notify the view or obtain data from the view
        public event EventHandler<NotificationEventArgs<Exception>> ErrorNotice;

        public ObservableCollection<Models.OneView.GuestProfile> GuestProfiles
        {
            get { return guestProfiles; }
            set
            {
                this.guestProfiles = value;
                NotifyPropertyChanged(m => m.GuestProfiles);

                // Raise can execute changed event on command
                CheckGroupEligiblityCommand.RaiseCanExecuteChanged();
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
                CheckGroupEligiblityCommand.RaiseCanExecuteChanged();
            }
        }
        public void CheckGroupEligibility()
        {
            List<String> guestIdList = new List<String>();

            foreach (Models.OneView.GuestProfile guestProfile in guestProfiles)
            {
                guestIdList.Add(guestProfile.XID);
            }

            base.Model = serviceAgent.CheckGroupEligibility(this.date.ToString("yyyy-MM-dd"), guestIdList);

            foreach (Models.GxP.IndividualEligibility i in base.Model.Individuals)
            {
               foreach(Models.OneView.GuestProfile guestProfile in this.guestProfiles)
               {
                   if (String.Compare(i.GuestId, guestProfile.XID, true) == 0)
                   {
                       i.FirstName = guestProfile.FirstName;
                       i.LastName = guestProfile.LastName;
                       break;
                   }
               }
            }
        }

        private bool CanCheckGroupEligiblity()
        {
            return this.date != null && this.guestProfiles.Count > 0;
        }

        private DelegateCommand checkGroupEligiblityCommand;
        public DelegateCommand CheckGroupEligiblityCommand
        {
            get { return checkGroupEligiblityCommand ?? 
                (checkGroupEligiblityCommand = new DelegateCommand(CheckGroupEligibility, CanCheckGroupEligiblity)); }
            private set { checkGroupEligiblityCommand = value; }
        }

        // Helper method to notify View of an error
        private void NotifyError(string message, Exception error)
        {
            // Notify view of an error
            Notify(ErrorNotice, new NotificationEventArgs<Exception>(message, error));
        }
    }
}