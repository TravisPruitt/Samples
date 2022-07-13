using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.GXP.Support.Models.IDMS
{
    public class GuestName : ModelBase<GuestName>
    {
        private string lastName;
        private string firstName;
        private string middleName;
        private string title;
        private string suffix;

        public string LastName
        {
            get { return this.lastName; }
            set
            {
                this.lastName = value;
                NotifyPropertyChanged(m => m.LastName);

            }
        }

        public string FirstName
        {
            get { return this.firstName; }
            set
            {
                this.firstName = value;
                NotifyPropertyChanged(m => m.FirstName);

            }
        }

        public string MiddleName
        {
            get { return this.middleName; }
            set
            {
                this.middleName = value;
                NotifyPropertyChanged(m => m.MiddleName);

            }
        }

        public string Title
        {
            get { return this.title; }
            set
            {
                this.title = value;
                NotifyPropertyChanged(m => m.Title);

            }
        }

        public string Suffix
        {
            get { return this.suffix; }
            set
            {
                this.suffix = value;
                NotifyPropertyChanged(m => m.Suffix);

            }
        }
    }
}
