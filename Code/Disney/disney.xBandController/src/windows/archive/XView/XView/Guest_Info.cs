using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using XViewLib;

namespace XView
{
    /// <summary>
    /// This class provides functions for manipulating (CRUD) guest info objects from a guest object.
    /// </summary>
    public class Guest_Info
    {

        /// <summary>
        /// Save (Create) a new guest_info object.
        /// </summary>
        /// <param name="guest_info">XViewLib.guest_info</param>
        public static void SaveGuestInfo(XViewLib.guest_info guest_info)
        {
            XViewLib.DataClasses1DataContext context = new DataClasses1DataContext();
            try
            {
                context.guest_infos.InsertOnSubmit(guest_info);
                context.SubmitChanges();
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }

        public static void DeleteGuestInfo(XViewLib.guest_info guest_info)
        {
            try
            {
                XViewLib.DataClasses1DataContext context = new DataClasses1DataContext();
                context.guest_infos.DeleteOnSubmit(guest_info);
                context.SubmitChanges();
            }
            catch (Exception ex)
            {
                
                throw ex;
            }


        }


    }
}