using System;
using System.Windows;
using System.Threading;
using System.Collections.ObjectModel;

// Toolkit namespace
using SimpleMvvmToolkit;
using System.Collections.Generic;

namespace WDW.NGE.Support.IDMSValidator.ViewModels
{
    /// <summary>
    /// This class contains properties that a View can data bind to.
    /// <para>
    /// Use the <strong>mvvmprop</strong> snippet to add bindable properties to this ViewModel.
    /// </para>
    /// </summary>
    public class GuestCompareViewModel : ViewModelDetailBase<GuestCompareViewModel, Models.GuestCompare>
    {
        private Models.Services.IOneViewServiceAgent oneViewServiceAgent;
        private Models.Services.IIDMSServiceAgent idmsServiceAgent;

        public GuestCompareViewModel()
        {
            this.oneViewServiceAgent = new Models.Services.OneViewServiceAgent();
            this.idmsServiceAgent = new Models.Services.IDMSServiceAgent();

            try
            {
                this.IsBusy = true;
                this.Model = new Models.GuestCompare();
            }
            finally
            {
                this.IsBusy = false;
            }
        }

        // TODO: Add events to notify the view or obtain data from the view
        public event EventHandler<NotificationEventArgs<Exception>> ErrorNotice;

        public String IdentifierType
        {
            get
            {
                if (this.Model != null)
                {
                    return this.Model.GuestIdentifier.IdentifierType;
                }
                return null;
            }
            set
            {
                this.Model.GuestIdentifier.IdentifierType = value;
                NotifyPropertyChanged(m => m.IdentifierType);

                // Raise can execute changed event on command
                GetGuestProfilesCommand.RaiseCanExecuteChanged();
            }
        }

        public String IdentifierValue
        {
            get
            {
                if (this.Model != null)
                {
                    return this.Model.GuestIdentifier.IdentifierValue;
                }
                return null;
            }
            set
            {
                this.Model.GuestIdentifier.IdentifierValue = value;
                NotifyPropertyChanged(m => m.IdentifierValue);

                // Raise can execute changed event on command
                GetGuestProfilesCommand.RaiseCanExecuteChanged();
            }
        }

        private bool isBusy;
        public bool IsBusy
        {
            get { return isBusy; }
            set
            {
                isBusy = value;
                NotifyPropertyChanged(m => m.IsBusy);
            }
        }
        private bool enabled;
        public bool Enabled
        {
            get { return enabled; }
            set
            {
                enabled = value;
                NotifyPropertyChanged(m => m.Enabled);
            }
        }

        public string MatchIndicator
        {
            get
            {
                if (this.Model.OneViewGuestProfile != null &&
                    this.Model.IdmsGuestProfile != null)
                {
                    if (this.Model.Status == Models.GuestCompareStatus.Match)
                    {
                        return "Matched";
                    }
                    else
                    {
                        return "Not Matched";
                    }
                }
                else
                {
                    return "";
                }
            }
        }

        // TODO: Add methods that will be called by the view

        // TODO: Optionally add callback methods for async calls to the service agent

        // Helper method to notify View of an error
        private void NotifyError(string message, Exception error)
        {
            // Notify view of an error
            Notify(ErrorNotice, new NotificationEventArgs<Exception>(message, error));
        }

        public void GetGuestProfiles()
        {
            try
            {
                this.IsBusy = true;
                this.Model.Compare();
                AddMissingIdentifiersCommand.RaiseCanExecuteChanged();
                AddMissingBandsCommand.RaiseCanExecuteChanged();
                RemoveExtraIdentifiersCommand.RaiseCanExecuteChanged();
                UpdateNameCommand.RaiseCanExecuteChanged();
                NotifyPropertyChanged(m => m.MatchIndicator);
            }
            finally
            {
                this.IsBusy = false;
            }
        }


        public void UpdateName()
        {
            try
            {
                this.IsBusy = true;
                this.Model.UpdateName();
                UpdateNameCommand.RaiseCanExecuteChanged();
                NotifyPropertyChanged(m => m.MatchIndicator);

            }
            finally
            {
                this.IsBusy = false;
            }
        }

        public void RemoveExtraIdentifiers()
        {
            try
            {
                this.IsBusy = true;
                this.Model.RemoveExtraIdentifiers();
                RemoveExtraIdentifiersCommand.RaiseCanExecuteChanged();
                NotifyPropertyChanged(m => m.MatchIndicator);
            }
            finally
            {
                this.IsBusy = false;
            }
        }

        public void AddMissingBands()
        {
            try
            {
                this.IsBusy = true;
                this.Model.AddMissingBands();
                AddMissingBandsCommand.RaiseCanExecuteChanged();
                NotifyPropertyChanged(m => m.MatchIndicator);
            }
            finally
            {
                this.IsBusy = false;
            }
        }

        public void AddMissingIdentifiers()
        {
            try
            {
                this.IsBusy = true;
                this.Model.AddMissingIdentifiers();
                AddMissingIdentifiersCommand.RaiseCanExecuteChanged();
                NotifyPropertyChanged(m => m.MatchIndicator);
            }
            finally
            {
                this.IsBusy = false;
            }
        }

        private bool CanUpdateName()
        {
            return (this.Model.Status & Models.GuestCompareStatus.NameMismatch) == Models.GuestCompareStatus.NameMismatch;
        }

        private bool CanRemoveExtraIdentifiers()
        {
            return (this.Model.Status & Models.GuestCompareStatus.ExtraIDMSIdentifiers) == Models.GuestCompareStatus.ExtraIDMSIdentifiers;
        }

        private bool CanAddMissingBands()
        {
            return (this.Model.Status & Models.GuestCompareStatus.MissingBands) == Models.GuestCompareStatus.MissingBands;
        }

        private bool CanAddMissingIdentifiers()
        {
            return (this.Model.Status & Models.GuestCompareStatus.MissingIdentifiers) == Models.GuestCompareStatus.MissingIdentifiers;
        }

        private bool CanGetGuestProfiles()
        {
            return !String.IsNullOrEmpty(this.Model.GuestIdentifier.IdentifierValue) &&
                !String.IsNullOrEmpty(this.Model.GuestIdentifier.IdentifierType);
        }

        private DelegateCommand getGuestProfilesCommand;
        public DelegateCommand GetGuestProfilesCommand
        {
            get { return getGuestProfilesCommand ?? (getGuestProfilesCommand = new DelegateCommand(GetGuestProfiles, CanGetGuestProfiles)); }
            private set { getGuestProfilesCommand = value; }
        }

        private DelegateCommand updateNameCommand;
        public DelegateCommand UpdateNameCommand
        {
            get { return updateNameCommand ?? (updateNameCommand = new DelegateCommand(UpdateName, CanUpdateName)); }
            private set { updateNameCommand = value; }
        }

        private DelegateCommand removeExtraIdentifiersCommand;
        public DelegateCommand RemoveExtraIdentifiersCommand
        {
            get { return removeExtraIdentifiersCommand ?? (removeExtraIdentifiersCommand = new DelegateCommand(RemoveExtraIdentifiers, CanRemoveExtraIdentifiers)); }
            set
            {
                removeExtraIdentifiersCommand = value;
                NotifyPropertyChanged(m => m.RemoveExtraIdentifiersCommand);
            }
        }

        private DelegateCommand addMissingBandsCommand;
        public DelegateCommand AddMissingBandsCommand
        {
            get { return addMissingBandsCommand ?? (addMissingBandsCommand = new DelegateCommand(AddMissingBands, CanAddMissingBands)); }
            set
            {
                addMissingBandsCommand = value;
                NotifyPropertyChanged(m => m.AddMissingBandsCommand);
            }
        }

        private DelegateCommand addMissingIdentifiersCommand;
        public DelegateCommand AddMissingIdentifiersCommand
        {
            get { return addMissingIdentifiersCommand ?? (addMissingIdentifiersCommand = new DelegateCommand(AddMissingIdentifiers, CanAddMissingIdentifiers)); }
            set
            {
                addMissingIdentifiersCommand = value;
                NotifyPropertyChanged(m => m.AddMissingIdentifiersCommand);
            }
        }
    }
}