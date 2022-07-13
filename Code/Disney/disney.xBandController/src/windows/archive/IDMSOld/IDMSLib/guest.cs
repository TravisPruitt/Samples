using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace IDMSLib
{
    public partial class guest
    {

        private ICollection<xbandPOCO> _xbands = null;

        public ICollection<xbandPOCO> XBands
        {
            get { return _xbands; }
            set { _xbands = value; }
        }


        public static List<guestPOCO> GetAllGuests()
        {
            

            XViewEntities context = new XViewEntities();

            List<guestPOCO> g = (from gu in context.guests
                                 select new guestPOCO
                                 {
                                    active = gu.active,
                                    guestId = gu.guestId,
                                    lastName = gu.lastName,
                                    firstName = gu.firstName,
                                    createdBy = gu.createdBy,
                                    createdDate = gu.createdDate,
                                    DOB = gu.DOB,
                                    updatedBy = gu.updatedBy,
                                    updatedDate = gu.updatedDate,
                                    sourceId = gu.sourceId,
                                    sourceTypeId = gu.sourceTypeId
                                 }

                             ).ToList<guestPOCO>();
            context.Connection.Close();
            return g;
        }

        public static guestPOCO GetGuestById(long Id)
        {
            XViewEntities context = new XViewEntities();

            guestPOCO g = (from gu in context.guests
                           where gu.guestId == Id
                           select new guestPOCO
                           {
                               active = gu.active,
                               guestId = gu.guestId,
                               lastName = gu.lastName,
                               firstName = gu.firstName,
                               createdBy = gu.createdBy,
                               createdDate = gu.createdDate,
                               DOB = gu.DOB,
                               updatedBy = gu.updatedBy,
                               updatedDate = gu.updatedDate,
                               sourceId = gu.sourceId,
                               sourceTypeId = gu.sourceTypeId
                           }).FirstOrDefault<guestPOCO>();

            return g;
        }

        public static void SaveGuest(guestPOCO newGuest)
        {
            guest g = new guest();
            g.active = newGuest.active;
            g.createdBy = newGuest.createdBy;
            g.createdDate = newGuest.createdDate;
            g.DOB = newGuest.DOB;
            g.firstName = newGuest.firstName;
            g.lastName = newGuest.lastName;
            g.updatedBy = newGuest.updatedBy;
            g.updatedDate = newGuest.updatedDate;
            g.sourceId = newGuest.sourceId;
            g.sourceTypeId = newGuest.sourceTypeId;

            XViewEntities context = new XViewEntities();

            context.guests.AddObject(g);
            context.SaveChanges();
            context.Connection.Close();

        }

        public static void UpdateGuest(guestPOCO updateGuest)
        {
            XViewEntities context = new XViewEntities();

            guest g = (from gu in context.guests
                       where gu.guestId == updateGuest.guestId
                       select gu).FirstOrDefault<guest>();

            g.active = updateGuest.active;
            g.createdBy = updateGuest.createdBy;
            g.createdDate = updateGuest.createdDate;
            g.DOB = updateGuest.DOB;
            g.firstName = updateGuest.firstName;
            g.lastName = updateGuest.lastName;
            g.updatedBy = updateGuest.updatedBy;
            g.updatedDate = updateGuest.updatedDate;
            g.sourceId = updateGuest.sourceId;
            g.sourceTypeId = updateGuest.sourceTypeId;

            context.SaveChanges();
            context.Connection.Close();

        }

        // Delete a guest from the database.
        public static void DeleteGuest(guestPOCO deleteGuest)
        {
            // Will have to remove all guest_infos and guest_xband associations first!
            XViewEntities context = new XViewEntities();

            try
            {
                guest g = (from gu in context.guests
                           where gu.guestId == deleteGuest.guestId
                           select gu).FirstOrDefault<guest>();

                context.guests.DeleteObject(g);
                context.SaveChanges();
            }
            catch (Exception)
            {

                throw;
            }
            finally
            {
                context.Connection.Close();
            }
        }
    }
}
