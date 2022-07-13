using System;
using System.Collections.Generic;
using System.Text;
using System.Globalization;

namespace xBRCMessageUtil
{
    public class GuestAction
    {
        public enum ActionType { Add, Move, Abandon, Done };
        private DateTime dt;
        private ActionType action;
        private string guestId;
        private bool bxpass;
        private string location;

        public GuestAction()
        {
            dt = DateTime.MinValue;
            action = ActionType.Add;
            bxpass = false;
            guestId = null;
            location = null;
        }

        public DateTime TimeStamp
        {
            get
            {
                return dt;
            }
            set
            {
                dt = value;
            }
        }

        public void SetTimeStamp(string s)
        {
            dt = new DateTime(1970, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc);
            try
            {
                dt = DateTime.ParseExact(s, "yyyy-MM-ddTHH:mm:ss.fff", CultureInfo.InvariantCulture, DateTimeStyles.RoundtripKind);
            }
            catch (Exception)
            {
            }

        }

        public ActionType Action
        {
            get
            {
                return action;
            }
            set
            {
                action = value;
            }
        }

        public string GuestId
        {
            get
            {
                return guestId;
            }
            set
            {
                guestId = value;
            }
        }

        public string Location
        {
            get
            {
                return location;
            }
            set
            {
                location = value;
            }
        }

        public bool xPass
        {
            get
            {
                return bxpass;
            }
            set
            {
                bxpass = value;
            }
        }
    }
}
