using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using XViewLib;

namespace XView
{
    public class guest_xbands
    {

        public static void RemoveAllXBandsFromGuest(guest guest)
        {
            
        }


        public static void RemoveXBandFromGuest(guest guest, xband xBand)
        {

            XViewLib.DataClasses1DataContext context = new DataClasses1DataContext();

            XViewLib.guest_xband xbands = (from gx in context.guest_xbands
                                           where gx.guestId == guest.guestId && gx.xbandId == xBand.xbandId
                                           select gx).FirstOrDefault<XViewLib.guest_xband>();

            context.guest_xbands.DeleteOnSubmit(xbands);
            context.SubmitChanges();
        
        }
    }
}