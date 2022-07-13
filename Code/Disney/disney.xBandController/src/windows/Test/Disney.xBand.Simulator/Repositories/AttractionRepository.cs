using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AutoMapper;

namespace Disney.xBand.Simulator.Repositories
{
    public interface IAttractionRepository
    {
        List<Dto.Attraction> GetAllActive();

        //Dto.Attraction Get(int id);

        //Dto.Attraction Create(Dto.Attraction attraction);

        //void Update(Dto.Attraction attraction);
    }

    public class AttractionRepository : IAttractionRepository
    {
        static AttractionRepository()
        {
            Mapper.Initialize(cfg =>
            {
                cfg.CreateMap<Dto.Attraction, Data.Attraction>();
                cfg.CreateMap<Data.Attraction, Dto.Attraction>();
                cfg.CreateMap<List<Dto.Attraction>, List<Data.Attraction>>();
                cfg.CreateMap<Dto.Controller, Data.Controller>();
                cfg.CreateMap<Data.Controller, Dto.Controller>();
                cfg.CreateMap<Dto.Reader, Data.Reader>();
                cfg.CreateMap<Data.Reader, Dto.Reader>();
                cfg.CreateMap<Dto.ReaderType, Data.ReaderType>();
                cfg.CreateMap<Data.ReaderType, Dto.ReaderType>();
                cfg.CreateMap<Dto.ReaderLocationType, Data.ReaderLocationType>();
                cfg.CreateMap<Data.ReaderLocationType, Dto.ReaderLocationType>();
            });
        }
         
        public List<Dto.Attraction> GetAllActive()
        {
            using (Data.SimulatorEntities context = new Data.SimulatorEntities())
            {
                var list = (from a in context.Attractions
                            where a.IsActive == true
                            select a).ToList();
                return Mapper.Map<List<Data.Attraction>, List<Dto.Attraction>>(list);
            }
        }

        //public Dto.Attraction Get(int id)
        //{
        //    using (Data.SimulatorEntities context = new Data.SimulatorEntities())
        //    {
        //        var attraction = (from a in context.Attractions
        //                          where a.AttractionID == id
        //                          select a).FirstOrDefault();

        //        return Mapper.Map<Data.Attraction, Dto.Attraction>(attraction);
        //    }
        //}

        //public Dto.Attraction Create(Dto.Attraction attraction)
        //{
        //    using (Data.SimulatorEntities context = new Data.SimulatorEntities())
        //    {
        //        Data.Attraction dataAttraction = Mapper.Map<Dto.Attraction, Data.Attraction>(attraction);

        //        context.AddToAttractions(dataAttraction);

        //        context.SaveChanges();

        //        attraction.AttractionID = dataAttraction.AttractionID;

        //        return attraction;
        //    }
        //}

        //public void Update(Dto.Attraction attraction)
        //{
        //    using (Data.SimulatorEntities context = new Data.SimulatorEntities())
        //    {
        //        Data.Attraction dataAttraction = Mapper.Map<Dto.Attraction, Data.Attraction>(attraction);

        //        context.Attach(dataAttraction);

        //        context.SaveChanges();
        //    }
        //}
    }
}
