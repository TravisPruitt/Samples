using System;
using System.Windows;
using System.Threading;
using System.Collections.ObjectModel;

// Toolkit namespace
using SimpleMvvmToolkit;
using System.Collections.Concurrent;
using System.IO;
using System.Threading.Tasks;
using System.Collections.Generic;

namespace WDW.NGE.Support.IDMSValidator.ViewModels
{
    /// <summary>
    /// This class contains properties that a View can data bind to.
    /// <para>
    /// Use the <strong>mvvmprop</strong> snippet to add bindable properties to this ViewModel.
    /// </para>
    /// </summary>
    public class CreateBandsViewModel : ViewModelBase<CreateBandsViewModel>
    {
        private BlockingCollection<string> queue;
        private Models.Services.IIDMSServiceAgent idmsServiceAgent;
        private Models.Services.IXBMSServiceAgent xbmsServiceAgent;
        private CancellationTokenSource tokenSource;

        // Default ctor
        public CreateBandsViewModel() 
        {
            this.ProcessingStatus = new ObservableCollection<Models.CreateBandStatusItem>();
            this.queue = new BlockingCollection<string>();
            this.startProcessingCommand = new DelegateCommand(StartProcessing, CanStartProcessing);
            this.cancelProcessingCommand = new DelegateCommand(CancelProcessing, CanCancelProcessing);
            this.findFileCommand = new DelegateCommand(FindFile, CanFindFile);
            this.idmsServiceAgent = new Models.Services.IDMSServiceAgent();
            this.xbmsServiceAgent = new Models.Services.XBMSServiceAgent();
        }

        private ObservableCollection<Models.CreateBandStatusItem> processingStatus;
        public ObservableCollection<Models.CreateBandStatusItem> ProcessingStatus
        {
            get { return processingStatus; }
            set
            {
                processingStatus = value;
                NotifyPropertyChanged(m => m.ProcessingStatus);
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

        private String filePath;
        public String FilePath
        {
            get { return filePath; }
            set
            {
                filePath = value;
                NotifyPropertyChanged(m => m.FilePath);
            }
        }
       
        private int totalCount;
        public int TotalCount
        {
            get { return totalCount; }
            set
            {
                totalCount = value;
                NotifyPropertyChanged(m => m.TotalCount);
            }
        }

        private int currentCount;
        public int CurrentCount
        {
            get { return currentCount; }
            set
            {
                currentCount = value;
                NotifyPropertyChanged(m => m.CurrentCount);
            }
        }

        private int processingPercentage;
        public int ProcessingPercentage
        {
            get { return processingPercentage; }
            set
            {
                processingPercentage = value;
                NotifyPropertyChanged(m => m.ProcessingPercentage);
            }
        }
        
        public event EventHandler<NotificationEventArgs<Exception>> ErrorNotice;

        public void StartProcessing()
        {
            try
            {
                this.IsBusy = true;

                LoadQueue();

                this.TotalCount = this.queue.Count;
                this.CurrentCount = 0;
                this.ProcessingStatus.Clear();
                this.tokenSource = new CancellationTokenSource();

                if (filePath != null)
                {
                    Task.Factory.StartNew(() =>
                    {
                        List<Task> tasks = new List<Task>();

                        for (int i = 0; i < 5; i++)
                        {
                            Task task = Task.Factory.StartNew(() =>
                            {
                                string xbandId = String.Empty;

                                while (queue.TryTake(out xbandId))
                                {
                                    Models.Services.ServiceResult<Models.xBMS.XbandDetails> xbandDetailsResult =
                                        xbmsServiceAgent.GetXbandDetails(xbandId);

                                    Models.CreateBandStatusItem statusItem = new Models.CreateBandStatusItem()
                                    {
                                        XbandId = xbandId
                                    };

                                    if (xbandDetailsResult.Status == Models.Services.ServiceCallStatus.OK)
                                    {
                                        Models.Services.ServiceResult<Models.Common.IGuestProfile> guestProfileResult =
                                            idmsServiceAgent.GetGuestProfile(new Models.Common.GuestIdentifier()
                                            {
                                                IdentifierType = xbandDetailsResult.Result.GuestIdType,
                                                IdentifierValue = xbandDetailsResult.Result.GuestId
                                            });


                                        if (guestProfileResult.Status == Models.Services.ServiceCallStatus.OK)
                                        {
                                            bool exists = false;
                                            //Check for band
                                            foreach (Models.Common.GuestIdentifier gi in guestProfileResult.Result.GuestIdentifiers)
                                            {
                                                if (String.Compare(gi.IdentifierValue, xbandId, true) == 0)
                                                {
                                                    exists = true;
                                                }
                                            }

                                            if (!exists)
                                            {
                                                //Create Band
                                                Models.IDMS.XbandAssociation xbandAssociation = new Models.IDMS.XbandAssociation();

                                                xbandAssociation.ExternalNumber = xbandDetailsResult.Result.ExternalNumber;
                                                xbandAssociation.LongRangeTag = xbandDetailsResult.Result.PublicId.ToString();
                                                if (!String.IsNullOrEmpty(xbandDetailsResult.Result.PrintedName))
                                                {
                                                    xbandAssociation.PrintedName = xbandDetailsResult.Result.PrintedName;
                                                }
                                                else
                                                {
                                                    xbandAssociation.PrintedName = "PrintedName "
                                                            + xbandDetailsResult.Result.PublicId.ToString();

                                                }

                                                xbandAssociation.PublicId = xbandDetailsResult.Result.PublicId.ToString();
                                                xbandAssociation.SecureId = xbandDetailsResult.Result.SecureId.ToString();
                                                xbandAssociation.ShortRangeTag = xbandDetailsResult.Result.ShortRangeTag.ToString();
                                                xbandAssociation.Uid = xbandDetailsResult.Result.ShortRangeTag.ToString();
                                                xbandAssociation.XbmsId = xbandDetailsResult.Result.XbandId;
                                                xbandAssociation.PrimaryState = xbandDetailsResult.Result.State;
                                                xbandAssociation.SecondaryState = xbandDetailsResult.Result.SecondaryState;
                                                xbandAssociation.GuestIdType = xbandDetailsResult.Result.GuestIdType;
                                                xbandAssociation.GuestIdValue = xbandDetailsResult.Result.GuestId;
                                                xbandAssociation.XbandOwnerId = xbandDetailsResult.Result.XbandOwnerId;
                                                if (!String.IsNullOrEmpty(xbandDetailsResult.Result.XbandRequest))
                                                {
                                                    xbandAssociation.XbandRequestId =
                                                        xbandDetailsResult.Result.XbandRequest.Replace("/xband-requests/", "");
                                                }

                                                if (!String.IsNullOrEmpty(xbandDetailsResult.Result.BandRole))
                                                {
                                                    xbandAssociation.BandType = xbandDetailsResult.Result.BandRole;
                                                }

                                                idmsServiceAgent.CreateXbandAssociation(xbandAssociation);

                                                statusItem.Status = Models.CreateBandStatus.Created;

                                            }
                                            else
                                            {
                                                statusItem.Status = Models.CreateBandStatus.BandExists;
                                            }
                                        }
                                        else
                                        {
                                            statusItem.Status = Models.CreateBandStatus.GuestNotFound;
                                        }
                                    }
                                    else
                                    {
                                        statusItem.Status = Models.CreateBandStatus.BandNotFound;
                                    }

                                    Application.Current.Dispatcher.Invoke(
                                        (Action)delegate
                                        {
                                            this.ProcessingStatus.Add(statusItem);
                                        });

                                    this.CurrentCount++;
                                    this.ProcessingPercentage = (this.CurrentCount * 100) / this.TotalCount;
                                }
                            });

                            tasks.Add(task);
                        }

                        Task.WaitAll(tasks.ToArray());

                    }).ContinueWith((t) =>
                    {
                        TaskCompleted();
                    });

                    CancelProcessingCommand.RaiseCanExecuteChanged();
                    StartProcessingCommand.RaiseCanExecuteChanged();
                    FindFileCommand.RaiseCanExecuteChanged();
                }
            }
            finally
            {
                //this.IsBusy = false;
            }
        }

        private void TaskCompleted()
        {
            Application.Current.Dispatcher.Invoke((Action)delegate
            {
                this.IsBusy = false;
                this.ProcessingPercentage = 0;
                StartProcessingCommand.RaiseCanExecuteChanged();
                CancelProcessingCommand.RaiseCanExecuteChanged();
                FindFileCommand.RaiseCanExecuteChanged();
            });
        }
        public void FindFile()
        {
            try
            {
                // Configure open file dialog box
                Microsoft.Win32.OpenFileDialog dlg = new Microsoft.Win32.OpenFileDialog();
                //dlg.FileName = "Document"; // Default file name
                dlg.DefaultExt = ".txt"; // Default file extension
                dlg.Filter = "Text documents (.txt)|*.txt"; // Filter files by extension 

                // Show open file dialog box
                Nullable<bool> result = dlg.ShowDialog();

                // Process open file dialog box results 
                if (result == true)
                {
                    // Open document 
                    this.FilePath = dlg.FileName;
                    StartProcessingCommand.RaiseCanExecuteChanged();
                }
            }
            finally
            {
            }
        }

        public void CancelProcessing()
        {
        }

        public bool CanStartProcessing()
        {
            return !this.IsBusy;
        }

        public bool CanCancelProcessing()
        {
            return this.IsBusy;
        }

        public bool CanFindFile()
        {
            return !this.IsBusy;
        }

        private DelegateCommand cancelProcessingCommand;
        public DelegateCommand CancelProcessingCommand
        {
            get { return cancelProcessingCommand; }
            set
            {
                cancelProcessingCommand = value;
                NotifyPropertyChanged(m => m.CancelProcessingCommand);
            }
        }

        private DelegateCommand findFileCommand;
        public DelegateCommand FindFileCommand
        {
            get { return findFileCommand; }
            set
            {
                findFileCommand = value;
                NotifyPropertyChanged(m => m.FindFileCommand);
            }
        }
        private DelegateCommand startProcessingCommand;
        public DelegateCommand StartProcessingCommand
        {
            get { return startProcessingCommand; }
            set
            {
                startProcessingCommand = value;
                NotifyPropertyChanged(m => m.StartProcessingCommand);
            }
        }

        private void LoadQueue()
        {
            this.queue = new BlockingCollection<string>();

            using (StreamReader sr = File.OpenText(this.FilePath))
            {
                String input = sr.ReadLine();

                while (input != null)
                {
                    try
                    {
                        this.queue.TryAdd(input);
                    }
                    catch (OperationCanceledException)
                    {
                        queue.CompleteAdding();
                        break;
                    }

                    //Read next line
                    input = sr.ReadLine();
                }
            }
        }

        // Helper method to notify View of an error
        private void NotifyError(string message, Exception error)
        {
            // Notify view of an error
            Notify(ErrorNotice, new NotificationEventArgs<Exception>(message, error));
        }
    }
}