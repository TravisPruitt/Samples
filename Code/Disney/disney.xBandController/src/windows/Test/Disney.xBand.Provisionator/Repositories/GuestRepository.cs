using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AutoMapper;

namespace Disney.xBand.Provisionator.Repositories
{
    public interface IGuestRepository
    {
        List<Dto.Guest> GetGuests(Dto.MagicBand band);
        void AddGuest(string firstName, string lastName, Dto.GuestType guestType);
        List<Dto.MagicBand> GetBands(Dto.Guest guest);
        List<Dto.MagicBand> GetActiveBands();
        List<Dto.Guest> FindGuests(string name);
        void AddGuestToBand(Dto.MagicBand band, Dto.Guest guest);
        void AddBandToGuest(Dto.Guest guest,Dto.MagicBand band);
        void RemoveBandFromGuest(Dto.Guest guest, Dto.MagicBand band);
    }

    public class GuestRepository : IGuestRepository
    {
        static GuestRepository()
        {
            Mapper.Initialize(cfg =>
            {
                cfg.CreateMap<Data.xband, Dto.MagicBand>()
                    .ForMember(dest => dest.BandID, opt => opt.MapFrom(src => src.bandId))
                    .ForMember(dest => dest.LongRangeID, opt => opt.MapFrom(src => src.longRangeId))
                    .ForMember(dest => dest.TapID, opt => opt.MapFrom(src => src.tapId))
                    .ForMember(dest => dest.MagicBandID, opt => opt.MapFrom(src => src.xbandId))
                    .ForMember(dest => dest.Guests, opt => opt.Ignore());

                cfg.CreateMap<Data.guest, Dto.Guest>()
                    .ForMember(dest => dest.GuestID, opt => opt.MapFrom(src => src.guestId))
                    .ForMember(dest => dest.FirstName, opt => opt.MapFrom(src => src.firstName))
                    .ForMember(dest => dest.LastName, opt => opt.MapFrom(src => src.lastName))
                    .ForMember(dest => dest.MagicBands, opt => opt.Ignore())
                    .ForMember(dest => dest.xID, opt => opt.MapFrom(src => src.source_system_link.Where(t => t.IDMS_Type.IDMSTypeName == "xid") == null ?
                            String.Empty : src.source_system_link.Where(t => t.IDMS_Type.IDMSTypeName == "xid").Single().sourceSystemIdValue));

                cfg.CreateMap<Data.guest_xband, Dto.MagicBand>()
                    .ForMember(dest => dest.MagicBandID, opt => opt.MapFrom(src => src.xband.xbandId))
                    .ForMember(dest => dest.BandID, opt => opt.MapFrom(src => src.xband.bandId))
                    .ForMember(dest => dest.LongRangeID, opt => opt.MapFrom(src => src.xband.longRangeId))
                    .ForMember(dest => dest.TapID, opt => opt.MapFrom(src => src.xband.tapId))
                    .ForMember(dest => dest.Guests, opt => opt.Ignore());
            });

            Mapper.AssertConfigurationIsValid();
        }

 
        public List<Dto.Guest> GetGuests(Dto.MagicBand band)
        {
            using (Data.IDMSEntities context = new Data.IDMSEntities())
            {

                var result = (from gx in context.guest_xband
                              join g in context.guests on gx.guestId equals g.guestId
                              where gx.xbandId == band.MagicBandID
                              select g).OrderBy(g => g.lastName).ToList();

                return Mapper.Map<List<Data.guest>, List<Dto.Guest>>(result);

            }
        }

        public void AddGuest(string firstName, string lastName, Dto.GuestType guestType)
        {
            using (Data.IDMSEntities context = new Data.IDMSEntities())
            {
                Data.IDMS_Type idmsType = (from i in context.IDMS_Type
                                           where i.IDMSTypeId == 19
                                           select i).Single();

                Data.IDMS_Type guestIdmsType = (from i in context.IDMS_Type
                                           where i.IDMSTypeId == 9
                                           select i).Single();

                Data.source_system_link link = new Data.source_system_link()
                {
                    IDMS_Type = idmsType,
                    sourceSystemIdValue = Guid.NewGuid().ToString().Replace("-",String.Empty).Replace("{",String.Empty).Replace("}",String.Empty).ToUpper()
                };

                Data.guest guest = new Data.guest()
                {
                    firstName = firstName,
                    lastName = lastName,
                    IDMSID = Guid.NewGuid(),
                    IDMS_Type = guestIdmsType,
                    VisitCount = 3,
                    active = true,
                    createdBy = "Provisioning",
                    createdDate = DateTime.UtcNow,
                    updatedBy = "Provisioning",
                    updatedDate = DateTime.UtcNow
                };

                guest.source_system_link.Add(link);

                context.SaveChanges();

            }
        }

        public List<Dto.MagicBand> GetBands(Dto.Guest guest)
        {
            using (Data.IDMSEntities context = new Data.IDMSEntities())
            {

                var result = (from gx in context.guest_xband
                              where gx.guestId == guest.GuestID
                              select gx).ToList();

                return Mapper.Map<List<Data.guest_xband>, List<Dto.MagicBand>>(result);
  
            }
        }

        public List<Dto.MagicBand> GetActiveBands()
        {
            using (Data.IDMSEntities context = new Data.IDMSEntities())
            {
                var result = (from x in context.xbands
                              where x.active == true
                              select x).OrderBy(x => x.bandId).ToList();

                return Mapper.Map<List<Data.xband>, List<Dto.MagicBand>>(result);
            }
        }

        public List<Dto.Guest> FindGuests(string name)
        {
            using (Data.IDMSEntities context = new Data.IDMSEntities())
            {

                //TODO: Add Not exists row in guest_xband
                var result = (from g in context.guests
                              where g.lastName.Contains(name) ||
                              g.firstName.Contains(name)
                              select new Dto.Guest
                    {
                        GuestID = g.guestId,
                        FirstName = g.firstName,
                        LastName = g.lastName,
                        xID = g.source_system_link.Where(x => x.IDMS_Type.IDMSTypeName == "xid").SingleOrDefault() == null ?
                            String.Empty : g.source_system_link.Where(x => x.IDMS_Type.IDMSTypeName == "xid").SingleOrDefault().sourceSystemIdValue
                    }).ToList();

                return result;
            }
        }

        public void AddGuestToBand(Dto.MagicBand band, Dto.Guest guest)
        {
            using (Data.IDMSEntities context = new Data.IDMSEntities())
            {
                Data.xband xband = (from x in context.xbands
                                           where x.xbandId == band.MagicBandID
                                           select x).Single();

                Data.guest_xband guest_xband = new Data.guest_xband()
                {
                    guestId = guest.GuestID,
                    xbandId = band.MagicBandID,
                    active = true,
                    createdBy = "Provisioning",
                    createdDate = DateTime.UtcNow,
                    updatedBy = "Provisioning",
                    updatedDate = DateTime.UtcNow
                };

                xband.guest_xband.Add(guest_xband);

                context.SaveChanges();

            }
        }

        public void AddBandToGuest(Dto.Guest guest, Dto.MagicBand band)
        {
            using (Data.IDMSEntities context = new Data.IDMSEntities())
            {
                Data.guest dataGuest = (from g in context.guests
                                    where g.guestId == guest.GuestID
                                    select g).Single();

                Data.guest_xband guest_xband = new Data.guest_xband()
                {
                    guestId = guest.GuestID,
                    xbandId = band.MagicBandID,
                    active = true,
                    createdBy = "Provisioning",
                    createdDate = DateTime.UtcNow,
                    updatedBy = "Provisioning",
                    updatedDate = DateTime.UtcNow
                };

                dataGuest.guest_xband.Add(guest_xband);

                context.SaveChanges();

            }
        }

        public void RemoveBandFromGuest(Dto.Guest guest,Dto.MagicBand band)
        {
            using (Data.IDMSEntities context = new Data.IDMSEntities())
            {
                Data.guest_xband guest_xband = (from gx in context.guest_xband
                                        where gx.guestId == guest.GuestID
                                        && gx.xbandId == band.MagicBandID
                                        select gx).Single();

                context.DeleteObject(guest_xband);

                context.SaveChanges();

            }
        }
    }
}
