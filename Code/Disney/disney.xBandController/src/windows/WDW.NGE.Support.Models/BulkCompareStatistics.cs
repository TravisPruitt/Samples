using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models
{
    public class BulkCompareStatistics : ModelBase<BulkCompareStatistics>
    {
        public BulkCompareStatistics()
        {
            this.stopwatch = new Stopwatch();
        }

        private Stopwatch stopwatch;

        public void Start()
        {
            this.stopwatch.Start();
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

        private int guestsFixed;
        public int GuestsFixed
        {
            get { return guestsFixed; }
            set
            {
                guestsFixed = value;
                NotifyPropertyChanged(m => m.GuestsFixed);
            }
        }

        private int guestsMatched;
        public int GuestsMatched
        {
            get { return guestsMatched; }
            set
            {
                guestsMatched = value;
                NotifyPropertyChanged(m => m.GuestsMatched);
            }
        }

        private int guestsMissingBands;
        public int GuestsMissingBands
        {
            get { return guestsMissingBands; }
            set
            {
                guestsMissingBands = value;
                NotifyPropertyChanged(m => m.GuestsMissingBands);
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

        public TimeSpan ElapsedTime
        {
            get { return this.stopwatch.Elapsed; }
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
    }
}
