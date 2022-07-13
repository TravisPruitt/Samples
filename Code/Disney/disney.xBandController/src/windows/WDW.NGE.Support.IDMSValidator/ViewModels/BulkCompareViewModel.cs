using System;
using System.Windows;
using System.Threading;
using System.Collections.ObjectModel;

// Toolkit namespace
using SimpleMvvmToolkit;
using System.ComponentModel;
using System.Collections.Concurrent;
using System.IO;
using System.Windows.Threading;
using System.Diagnostics;
using System.Text;
using System.Threading.Tasks;
using System.Collections.Generic;

namespace WDW.NGE.Support.IDMSValidator.ViewModels
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
    public class BulkCompareViewModel : ViewModelBase<BulkCompareViewModel>
    {

        private BlockingCollection<string> queue;
        private CancellationTokenSource tokenSource;

        // Default ctor
        public BulkCompareViewModel()
        {
            this.ProcessingStatus = new ObservableCollection<Models.BulkCompareStatusItem>();
            this.queue = new BlockingCollection<string>();
            this.statistics = new Models.BulkCompareStatistics();
            this.MissingBandIds = new ObservableCollection<string>();
            this.copyMissingBandsCommand = new DelegateCommand(CopyMissingBands, CanCopyMissingBands);
            this.startProcessingCommand = new DelegateCommand(StartProcessing, CanStartProcessing);
            this.cancelProcessingCommand = new DelegateCommand(CancelProcessing, CanCancelProcessing);
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

        private ObservableCollection<Models.BulkCompareStatusItem> processingStatus;
        public ObservableCollection<Models.BulkCompareStatusItem> ProcessingStatus
        {
            get { return processingStatus; }
            set
            {
                processingStatus = value;
                NotifyPropertyChanged(m => m.ProcessingStatus);
            }
        }

        private ObservableCollection<String> missingBandIds;
        public ObservableCollection<String> MissingBandIds
        {
            get { return missingBandIds; }
            set
            {
                missingBandIds = value;
                NotifyPropertyChanged(m => m.MissingBandIds);
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

        private Models.BulkCompareStatistics statistics;
        public Models.BulkCompareStatistics Statistics
        {
            get { return statistics; }
            set
            {
                statistics = value;
                NotifyPropertyChanged(m => m.Statistics);
            }
        }

        public event EventHandler<NotificationEventArgs<Exception>> ErrorNotice;

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

        public bool CanFindFile()
        {
            return !this.IsBusy;
        }

        public void CancelProcessing()
        {
            if (this.tokenSource != null)
            {
                this.tokenSource.Cancel();
                CancelProcessingCommand.RaiseCanExecuteChanged();
            }
        }

        public bool CanCancelProcessing()
        {
            if (this.tokenSource != null)
            {
                return !this.tokenSource.IsCancellationRequested;
            }

            return false;
        }

        public void StartProcessing()
        {
            try
            {
                this.IsBusy = true;
                this.ProcessingStatus.Clear();
                this.MissingBandIds.Clear();
                this.Statistics = new Models.BulkCompareStatistics();
                this.Statistics.Start();

                LoadQueue();

                this.Statistics.TotalCount = this.queue.Count;

                this.tokenSource = new CancellationTokenSource();

                Task.Factory.StartNew(() =>
                {
                    List<Task> tasks = new List<Task>();

                    for (int i = 0; i < 5; i++)
                    {
                        Task task = Task.Factory.StartNew(() =>
                        {
                            CompareGuests(this.FilePath, this.tokenSource.Token);
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
                CopyMissingBandsCommand.RaiseCanExecuteChanged();
                FindFileCommand.RaiseCanExecuteChanged();
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
                               this.Statistics.ProcessingPercentage = 0;
                               StartProcessingCommand.RaiseCanExecuteChanged();
                               CancelProcessingCommand.RaiseCanExecuteChanged();
                               FindFileCommand.RaiseCanExecuteChanged();
                           });
        }

        private void CompareGuests(string filePath, CancellationToken token)
        {
            if (filePath != null)
            {
                Models.GuestCompare guestCompare = new Models.GuestCompare();

                string input = String.Empty;

                while (queue.TryTake(out input))
                {
                    if (token.IsCancellationRequested)
                    {
                        break;
                    }

                    string[] fields = input.Split(',');

                    string identifierType = fields[1];
                    string identifierValue = fields[0];

                    Models.Common.GuestIdentifier gi = new Models.Common.GuestIdentifier()
                    {
                        IdentifierType = identifierType,
                        IdentifierValue = identifierValue
                    };

                    guestCompare.GuestIdentifier = gi;
                    guestCompare.Compare();

                    Models.GuestCompareStatus status = guestCompare.Status;

                    guestCompare.Fix();
                    guestCompare.Compare();

                    if (status != Models.GuestCompareStatus.Match && guestCompare.Status == Models.GuestCompareStatus.Match)
                    {
                        status = Models.GuestCompareStatus.Fixed;
                    }
                    else
                    {
                        status = guestCompare.Status;
                    }


                    Application.Current.Dispatcher.Invoke(
                        (Action)delegate
                        {
                            lock (this.Statistics)
                            {
                                this.Statistics.CurrentCount++;

                                if (status == Models.GuestCompareStatus.Match)
                                {
                                    this.Statistics.GuestsMatched++;
                                }
                                else if (status == Models.GuestCompareStatus.Fixed)
                                {
                                    this.Statistics.GuestsFixed++;
                                }
                                else if ((status & Models.GuestCompareStatus.MissingBands) == Models.GuestCompareStatus.MissingBands)
                                {
                                    this.Statistics.GuestsMissingBands++;
                                    foreach (Models.Common.GuestIdentifier guestIdentifier in
                                        guestCompare.OneViewGuestProfile.GuestIdentifiers)
                                    {
                                        if (String.Compare(guestIdentifier.IdentifierType, "xbandid", true) == 0 &&
                                            guestIdentifier.Match == false)
                                        {
                                            this.MissingBandIds.Add(guestIdentifier.IdentifierValue);
                                        }
                                    }
                                    NotifyPropertyChanged(m => m.MissingBandIds);
                                    CopyMissingBandsCommand.RaiseCanExecuteChanged();
                                }

                                this.ProcessingStatus.Add(new Models.BulkCompareStatusItem()
                                {
                                    GuestIdentifier = new Models.Common.GuestIdentifier()
                                    {
                                        IdentifierType = identifierType,
                                        IdentifierValue = identifierValue
                                    },
                                    Status = status
                                });

                                Statistics.ProcessingPercentage = (this.Statistics.CurrentCount * 100) / this.Statistics.TotalCount;
                                NotifyPropertyChanged(m => m.Statistics);
                            }

                        });
                }
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

        public bool CanStartProcessing()
        {
            return !this.IsBusy;
        }

        private bool CanCopyMissingBands()
        {
            return this.MissingBandIds.Count > 0;
        }

        private void CopyMissingBands()
        {
            StringBuilder sb = new StringBuilder();

            foreach (String bandId in this.MissingBandIds)
            {
                sb.AppendLine(bandId);
            }

            Clipboard.SetData(DataFormats.Text, sb.ToString());
        }

        private DelegateCommand findFileCommand;
        public DelegateCommand FindFileCommand
        {
            get { return findFileCommand ?? (findFileCommand = new DelegateCommand(FindFile, CanFindFile)); }
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

        private DelegateCommand copyMissingBandsCommand;
        public DelegateCommand CopyMissingBandsCommand
        {
            get { return copyMissingBandsCommand; }
            set
            {
                copyMissingBandsCommand = value;
                NotifyPropertyChanged(m => m.CopyMissingBandsCommand);
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