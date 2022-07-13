using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.Common
{
    public class Link : ModelBase<Link>
    {
        private String href;

         public string Href
        {
            get
            {
                return this.href;
            }
            set
            {
                this.href = value;
                NotifyPropertyChanged(m => m.Href);

            }
        }
    }
}
