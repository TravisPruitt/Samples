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
    public class FastPassIntegrationViewModel : ViewModelDetailBase<FastPassIntegrationViewModel, Models.GxP.FastPassIntegration>
    {
        private Models.Services.IGxPServiceAgent serviceAgent;

        // Default ctor
        public FastPassIntegrationViewModel()
        {
            this.serviceAgent = new Models.Services.GxPServiceAgent();
        }

        public event EventHandler<NotificationEventArgs<Exception>> ErrorNotice;


        // TODO: Add methods that will be called by the view

        // TODO: Optionally add callback methods for async calls to the service agent
        
        // Helper method to notify View of an error
        private void NotifyError(string message, Exception error)
        {
            // Notify view of an error
            Notify(ErrorNotice, new NotificationEventArgs<Exception>(message, error));
        }
    }
}