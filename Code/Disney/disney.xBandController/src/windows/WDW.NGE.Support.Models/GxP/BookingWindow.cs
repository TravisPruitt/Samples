using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.GxP
{
    public class BookingWindow : ModelBase<BookingWindow>
    {
        private String endTime;
        private String startTime;

        public String EndTime
        {
            get
            {
                if (this.endTime != null)
                {
                    return DateTime.Parse(this.endTime).ToString("yyyy-MM-dd");
                }
                return null;
            }
            set
            {
                this.endTime = value;
                NotifyPropertyChanged(m => m.EndTime);

            }
        }

        public string StartTime
        {
            get
            {
                if (this.startTime != null)
                {
                    return DateTime.Parse(this.startTime).ToString("yyyy-MM-dd");
                }
                return null;
            }
            set
            {
                this.startTime = value;
                NotifyPropertyChanged(m => m.StartTime);

            }
        }
    }
}
