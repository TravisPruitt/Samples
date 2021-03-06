//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections.Specialized;

namespace IDMSLib
{
    public partial class xbandPOCO
    {
        #region Primitive Properties
    
        public virtual long xbandId
        {
            get;
            set;
        }
    
        public virtual Nullable<long> bandId
        {
            get;
            set;
        }
    
        public virtual Nullable<long> longRangeId
        {
            get;
            set;
        }
    
        public virtual Nullable<long> tapId
        {
            get;
            set;
        }
    
        public virtual Nullable<long> secureId
        {
            get;
            set;
        }
    
        public virtual Nullable<long> UID
        {
            get;
            set;
        }
    
        public virtual string bandFriendlyName
        {
            get;
            set;
        }
    
        public virtual string printedName
        {
            get;
            set;
        }
    
        public virtual Nullable<bool> active
        {
            get;
            set;
        }
    
        public virtual string createdBy
        {
            get;
            set;
        }
    
        public virtual Nullable<System.DateTime> createdDate
        {
            get;
            set;
        }
    
        public virtual string updatedBy
        {
            get;
            set;
        }
    
        public virtual Nullable<System.DateTime> updatedDate
        {
            get;
            set;
        }
    
        public virtual string sourceId
        {
            get;
            set;
        }
    
        public virtual Nullable<long> sourceTypeId
        {
            get;
            set;
        }

        #endregion
        #region Navigation Properties
    
        public virtual ICollection<guest_xband> guest_xband
        {
            get
            {
                if (_guest_xband == null)
                {
                    var newCollection = new FixupCollection<guest_xband>();
                    newCollection.CollectionChanged += Fixupguest_xband;
                    _guest_xband = newCollection;
                }
                return _guest_xband;
            }
            set
            {
                if (!ReferenceEquals(_guest_xband, value))
                {
                    var previousValue = _guest_xband as FixupCollection<guest_xband>;
                    if (previousValue != null)
                    {
                        previousValue.CollectionChanged -= Fixupguest_xband;
                    }
                    _guest_xband = value;
                    var newValue = value as FixupCollection<guest_xband>;
                    if (newValue != null)
                    {
                        newValue.CollectionChanged += Fixupguest_xband;
                    }
                }
            }
        }
        private ICollection<guest_xband> _guest_xband;

        #endregion
        #region Association Fixup
    
        private void Fixupguest_xband(object sender, NotifyCollectionChangedEventArgs e)
        {
            if (e.NewItems != null)
            {
                foreach (guest_xband item in e.NewItems)
                {
                    item.xbandId = xbandId;
                }
            }
    
            if (e.OldItems != null)
            {
                foreach (guest_xband item in e.OldItems)
                {
                    item.xbandId = null;
                }
            }
        }

        #endregion
    }
}
