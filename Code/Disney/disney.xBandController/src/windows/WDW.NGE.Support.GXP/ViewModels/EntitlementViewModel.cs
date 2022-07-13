using System;
using System.Windows;
using System.Threading;
using System.Collections.ObjectModel;

// Toolkit namespace
using SimpleMvvmToolkit;
using System.Collections.Generic;
using System.Net;

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
    public class EntitlementViewModel : ViewModelDetailBase<EntitlementViewModel, Models.OneView.Entitlement>
    {
        private Models.Services.IOneViewServiceAgent serviceAgent;
        private string reservationId;
        private DateTime date;
        private bool isBusy;

        // Default ctor
        public EntitlementViewModel()
            : this(new Models.Services.OneViewServiceAgent())
        {
        }

        public EntitlementViewModel(Models.OneView.Entitlement model)
        {
            base.Model = model;
        }

        public EntitlementViewModel(Models.Services.IOneViewServiceAgent serviceAgent)
        {
            this.serviceAgent = serviceAgent;
            this.IsBusy = false;
            this.GroupEligibilityViewModel = new GroupEligibilityViewModel();
            this.date = DateTime.Now;
        }

        public event EventHandler<NotificationEventArgs<Exception>> ErrorNotice;

        public bool IsBusy
        {
            get { return this.isBusy; }
            set
            {
                this.isBusy = value;
                NotifyPropertyChanged(m => m.IsBusy);
            }
        }

        public string ReservationId
        {
            get { return this.reservationId; }
            set
            {
                this.reservationId = value;
                NotifyPropertyChanged(m => m.ReservationId);

                // Raise can execute changed event on command
                GetEntitlementCommand.RaiseCanExecuteChanged();
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
                GetEntitlementCommand.RaiseCanExecuteChanged();
            }
        }

        public GroupEligibilityViewModel GroupEligibilityViewModel { get; private set; }

        public void GetEntitlement()
        {
            this.IsBusy = true;

            try
            {
                base.Model = serviceAgent.GetEntitlement(this.reservationId);

                if (base.Model != null)
                {
                    //Not sure if this is the right way to do this.
                    this.GroupEligibilityViewModel.GuestProfiles = 
                        new ObservableCollection<Models.OneView.GuestProfile>(base.Model.GuestProfiles);
                    this.GroupEligibilityViewModel.Date = this.date;
                    this.GroupEligibilityViewModel.CheckGroupEligibility();
                }
            }
            catch (WebException ex)
            {
                var response = ex.Response as HttpWebResponse;
                if(response != null)
                {
                    if (response.StatusCode == HttpStatusCode.InternalServerError)
                    {
                        this.NotifyError("Invalid request.", ex);
                    }
                    else
                    {
                        this.NotifyError("Can't connect to server.", ex);
                    }
                }
            }
            catch (Exception ex)
            {
                this.NotifyError(String.Format("Unexpected Error: {0}",ex.Message), ex);
            }
            finally
            {
                this.IsBusy = false;
            }
        }

        public bool CanGetEntitlement()
        {
            return !String.IsNullOrEmpty(this.reservationId) && this.date != null;
        }

        private DelegateCommand getEntitlementCommand;
        public DelegateCommand GetEntitlementCommand
        {
            get
            {
                return getEntitlementCommand ?? (getEntitlementCommand = 
                new DelegateCommand(GetEntitlement, CanGetEntitlement)); }
            private set { getEntitlementCommand = value; }
        }

        // TODO: Optionally add callback methods for async calls to the service agent
        
        // Helper method to notify View of an error
        private void NotifyError(string message, Exception error)
        {
            // Notify view of an error
            Notify(ErrorNotice, new NotificationEventArgs<Exception>(message, error));
        }
    }
}