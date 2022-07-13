using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace XView
{
    public class ExceptionGuestDoesNotExist : Exception
    {
        private string _id = string.Empty;
        
        public ExceptionGuestDoesNotExist(string Id) : base("Guest with Id " + Id + " was not found in the database.")
        {
            _id = Id;

        }

        public override string Message
        {
            get
            {
                return base.Message;
            }
        }

        public String GuestId
        {
            get
            {
                return _id;
            }
        }


    }

    public class ExceptionNoGuestsFound : Exception
    {
        public ExceptionNoGuestsFound()
            : base("No guests were found in the guests database!")
        {
        }
    }
}