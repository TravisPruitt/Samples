using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;

namespace Disney.xBand.Simulator.Dto
{
    public class DtoObjectBase : INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler PropertyChanged;

        protected bool HasChanges { get; private set; }

        protected void OnPropertyChanged(string propertyName)
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this, new PropertyChangedEventArgs(propertyName));
                this.HasChanges = true;
            }
        }

        public void SaveChanges()
        {
            this.HasChanges = false;
        }
    }
}
