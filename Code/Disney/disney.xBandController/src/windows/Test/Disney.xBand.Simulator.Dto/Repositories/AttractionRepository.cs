using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AutoMapper;

namespace Disney.xBand.Simulator.Dto.Repositories
{
    public interface IAttractionRepository
    {
        List<Dto.Attraction> GetAllActive();
        List<Dto.Statistics> GetStatistics(int attractionID);
        List<Dto.Guest> GetGuestPositions(int attractionID);
        List<Dto.GuestCountItem> GetGuestStates(int attractionID);
     }

    public class AttractionRepository : IAttractionRepository
    {
        static AttractionRepository()
        {
            Mapper.Initialize(cfg =>
            {
                cfg.CreateMap<Dto.Attraction, Data.Attraction>()
                    .ForMember(dest => dest.EntityKey, opt => opt.Ignore())
                    .ForMember(dest => dest.ControllerReference, opt => opt.Ignore());
                cfg.CreateMap<Data.Attraction, Dto.Attraction>();
                cfg.CreateMap<Dto.Controller, Data.Controller>()
                    .ForMember(dest => dest.AttractionReference, opt => opt.Ignore())
                    .ForMember(dest => dest.EntityKey, opt => opt.Ignore());
                cfg.CreateMap<Data.Controller, Dto.Controller>();
                cfg.CreateMap<Dto.Reader, Data.Reader>()
                    .ForMember(dest => dest.ControllerReference, opt => opt.Ignore())
                    .ForMember(dest => dest.ControllerID, opt => opt.Ignore())
                    .ForMember(dest => dest.EntityKey, opt => opt.Ignore())
                    .ForMember(dest => dest.ReaderLocationTypeReference, opt => opt.Ignore())
                    .ForMember(dest => dest.ReaderLocationTypeID, opt => opt.Ignore())
                    .ForMember(dest => dest.ReaderTypeReference, opt => opt.Ignore())
                    .ForMember(dest => dest.ReaderTypeID, opt => opt.Ignore())
                    .ForMember(dest => dest.ReaderEvents, opt => opt.Ignore());
                cfg.CreateMap<Data.Reader, Dto.Reader>();
                cfg.CreateMap<Dto.ReaderType, Data.ReaderType>()
                    .ForMember(dest => dest.EntityKey, opt => opt.Ignore())
                    .ForMember(dest => dest.Readers, opt => opt.Ignore());
                cfg.CreateMap<Data.ReaderType, Dto.ReaderType>();
                cfg.CreateMap<Dto.ReaderLocationType, Data.ReaderLocationType>()
                    .ForMember(dest => dest.EntityKey, opt => opt.Ignore())
                    .ForMember(dest => dest.Readers, opt => opt.Ignore());
                cfg.CreateMap<Data.ReaderLocationType, Dto.ReaderLocationType>();
                cfg.CreateMap<Dto.Statistics,Data.GetStatistics_Result>();
                cfg.CreateMap<Data.GetStatistics_Result, Dto.Statistics>();
                cfg.CreateMap<List<Dto.Statistics>, List<Data.GetStatistics_Result>>();
                cfg.CreateMap<List<Data.GetStatistics_Result>, List<Dto.Statistics>>();
            });

            Mapper.AssertConfigurationIsValid();
        }
         
        public List<Dto.Attraction> GetAllActive()
        {
            using (Data.SimulatorEntities context = new Data.SimulatorEntities())
            {
                var list = (from a in context.Attractions
                            select a).ToList();
                return Mapper.Map<List<Data.Attraction>, List<Dto.Attraction>>(list);
            }
        }

        public List<Dto.Statistics> GetStatistics(int attractionID)
        {
            using (Data.SimulatorEntities context = new Data.SimulatorEntities())
            {
                var result = context.GetStatistics(attractionID);

                return result.Select(s => new Statistics()
                {
                    Bands = s.Bands.Value,
                    Reads = s.Reads.Value,
                    ReaderLocationTypeName = s.ReaderLocationTypeName,
                    ReaderTypeName = s.ReaderTypeName

                }).ToList();
            }
        }

        public List<Dto.Guest> GetGuestPositions(int attractionID)
        {
            using (Data.SimulatorEntities context = new Data.SimulatorEntities())
            {
                var result = context.GetGuestPositions(attractionID);

                return result.Select(g => new Guest()
                {
                    GuestID = g.GuestID,
                    FirstName = g.FirstName,
                    LastName = g.LastName,
                    EntryTime = g.EntryTime,
                    GuestState = (Dto.GuestState) g.GuestState,
                    HasFastPassPlus = g.HasFastPassPlus,
                    xPosition = g.xPosition,
                    yPosition = g.yPosition
                }).ToList();

            }

        }

        public List<Dto.GuestCountItem> GetGuestStates(int attractionID)
        {
            using (Data.SimulatorEntities context = new Data.SimulatorEntities())
            {
                var result = context.GetGuestStates(attractionID);

                return result.Select(g => new GuestCountItem()
                {
                    GuestCount = g.GuestCount.Value,
                    State = g.State
                }).ToList();
            }
        }
    }
}
