using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.ServiceModel;
using System.ServiceModel.Activation;
using System.ServiceModel.Web;
using XViewLib;

using System.Web.Script.Serialization;
using System.IO;
using System.Text;
using System.Data.Linq;

namespace XView
{

    public class Guests
    {

        public static List<guest> getGuests()
        {
            List<guest> retVal = null;

            try
            {
                retVal = GetAllGuests();
                if (retVal != null && retVal.Count == 0)
                {
                    throw new ExceptionNoGuestsFound();
                }
            }
            catch (Exception ex)
            {  
                throw ex;
            }

            
            return retVal;
        }

        /// <summary>
        /// get a guest by the guestId. (XView generated guestId.)
        /// </summary>
        /// <param name="Id"></param>
        /// <returns></returns>
        public static guest getGuestbyId(string Id)
        {
            long guestId = long.Parse(Id);
            guest retVal = null;

            try
            {
                retVal = GetGuest(guestId);

                if (retVal == null || retVal.guestId == 0)
                {
                    throw new ExceptionGuestDoesNotExist(Id);
                }

                retVal.guest_info = GetGuestInfo(guestId);
                retVal.xbands = GetGuestXBands(guestId);

            }
            catch (Exception)
            {
                
                throw;
            }

            return retVal;
        }

        /// <summary>
        /// Save a guest object to the database.
        /// This also will save a guestInfo object if there is one attached to the user.
        /// </summary>
        /// <param name="guest"></param>
        /// <returns></returns>
        public static bool SaveGuest(guest guest)
        {
            bool retVal = false;

            XViewLib.DataClasses1DataContext context = new DataClasses1DataContext();
            context.guests.InsertOnSubmit(guest);

            context.SubmitChanges();

            if (guest.guest_info != null)
            {
                Guest_Info.SaveGuestInfo(guest.guest_info);
            }

            return retVal;

        }

        public List<guest> SearchGuests(String name)
        {
            List<guest> retVal = null;

            return retVal;
        }

        public HttpResponse UpdateGuest(guest guest)
        {
            HttpResponse retVal = null;

            return retVal;
        }

        public HttpResponse CreateGuest(guest guest)
        {
            HttpResponse retVal = null;

            return retVal;
        }

        public HttpResponse CreateDemoGuest(guest guest)
        {
            HttpResponse retVal = null;

            return retVal;
        }

        public void DeleteGuest(guest guest)
        {
            try
            {
                if (guest.guest_info != null)
                {
                    Guest_Info.DeleteGuestInfo(guest.guest_info);
                }

                XViewLib.DataClasses1DataContext context = new DataClasses1DataContext();
                context.guests.DeleteOnSubmit(guest);

                // Remove all xband associations for the guest being deleted.


                context.SubmitChanges();


            }
            catch (Exception ex)
            {
                
                throw ex;
            }
        }

        public HttpResponse AddXBandToGuest(String guestId, String xBandId)
        {
            HttpResponse retVal = null;

            return retVal;
        }

        public HttpResponse RemoveXBandFromGuest(String guestId, String xBandId)
        {
            HttpResponse retVal = null;

            return retVal;
        }


        private static List<guest> GetAllGuests()
        {
            List<guest> retVal = null;

            XViewLib.DataClasses1DataContext context = new DataClasses1DataContext();

            retVal = (from guests in context.guests
                      select guests).ToList<guest>();

            // Load all the guests, guest infos and xbands.
            if (retVal != null)
            {
                foreach (guest g in retVal)
                {
                    g.guest_info = GetGuestInfo(g.guestId);
                    g.xbands = GetGuestXBands(g.guestId);
                }
            }



            return retVal;
        }

        private static guest GetGuest(long Id)
        {
            guest retVal = null;

            XViewLib.DataClasses1DataContext context = new DataClasses1DataContext();

            retVal = (from x in context.guests
                     where x.guestId == Id
                     select x).FirstOrDefault<guest>();

            return retVal;
        }

        private static guest_info GetGuestInfo(long Id)
        {
            guest_info retVal = null;

            XViewLib.DataClasses1DataContext context = new DataClasses1DataContext();

            retVal = (from xi in context.guest_infos
                             where xi.guestId == Id
                             select xi).FirstOrDefault<guest_info>();

            return retVal;
        }

        private static List<xband> GetGuestXBands(long Id)
        {
            List<xband> retVal = null;

            XViewLib.DataClasses1DataContext context = new DataClasses1DataContext();

            retVal = (from xb in context.guest_xbands
                                 join xbd in context.xbands on xb.xbandId equals xbd.xbandId
                                 where xb.guestId == Id
                                 select xbd).ToList<xband>();


            return retVal;
        }


    }
}