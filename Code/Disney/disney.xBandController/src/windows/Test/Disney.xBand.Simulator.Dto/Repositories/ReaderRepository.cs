using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Disney.xBand.Simulator.Dto;
using AutoMapper;

namespace Disney.xBand.Simulator.Dto.Repositories
{
    public interface IReaderRepository
    {
        bool IsActive(int readerID);

        Status GetStatus(int readerID);
        void UpdateName(int readerID, string name);
        void UpdateStreamInfo(int readerID, string url, int interval, int maximumEvents);

        Hello GetHello(int readerID);
        Dto.TapReaderEvents GetTapReaderEvents(int readerID, string bandID, long sinceEventNumber, int maximumEvents);
        Dto.LongRangeReaderEvents GetLongRangeReaderEvents(int readerID, string bandID, long sinceEventNumber, int maximumEvents);

        long GetLastUpstreamEvent(int readerID);
        void SetLastUpstreamEvent(int readerID, long lastUpstreamEvent);
    }

    public class ReaderRepository : IReaderRepository
    {
        private const string TIME_FORMAT = "yyyy-MM-ddTHH:mm:ss.fff";
        
        static ReaderRepository()
        {
            Mapper.Initialize(cfg =>
            {
                cfg.CreateMap<Dto.Reader, Data.Reader>();
                cfg.CreateMap<Data.Reader, Dto.Reader>();
                cfg.CreateMap<List<Dto.Reader>, List<Data.Reader>>();

                cfg.CreateMap<Dto.Controller, Data.Controller>();
                cfg.CreateMap<Data.Controller, Dto.Controller>();

                cfg.CreateMap<Dto.ReaderType, Data.ReaderType>();
                cfg.CreateMap<Data.ReaderType, Dto.ReaderType>();

                cfg.CreateMap<Dto.ReaderLocationType, Data.ReaderLocationType>();
                cfg.CreateMap<Data.ReaderLocationType, Dto.ReaderLocationType>();

                cfg.CreateMap<Dto.LongRangeReaderEvent, Data.ReaderEvent>();
                cfg.CreateMap<Data.ReaderEvent, Dto.LongRangeReaderEvent>();
                cfg.CreateMap<List<Dto.LongRangeReaderEvent>, List<Data.ReaderEvent>>();

                cfg.CreateMap<Dto.TapReaderEvent, Data.ReaderEvent>();
                cfg.CreateMap<Data.ReaderEvent, Dto.TapReaderEvent>();
                cfg.CreateMap<List<Dto.TapReaderEvent>, List<Data.ReaderEvent>>();

                cfg.CreateMap<Dto.TapReaderEvent, Data.GetReaderEvents_Result>();
                cfg.CreateMap<Data.GetReaderEvents_Result, Dto.TapReaderEvent>();
                cfg.CreateMap<List<Dto.TapReaderEvent>, List<Data.GetReaderEvents_Result>>();

                cfg.CreateMap<Dto.LongRangeReaderEvent, Data.GetReaderEvents_Result>();
                cfg.CreateMap<Data.GetReaderEvents_Result, Dto.LongRangeReaderEvent>();
                cfg.CreateMap<List<Dto.LongRangeReaderEvent>, List<Data.GetReaderEvents_Result>>();
            });
        }

        public bool IsActive(int readerID)
        {
            using (Simulator.Data.SimulatorEntities context = new Simulator.Data.SimulatorEntities())
            {
                var active = (from r in context.Readers
                              where r.ReaderID == readerID
                              select r.IsActive).Single();

                return active;
            }
        }

        public Dto.Hello GetHello(int readerID)
        {
            using (Simulator.Data.SimulatorEntities context = new Simulator.Data.SimulatorEntities())
            {
                int? maxEventNumber = (from re in context.ReaderEvents
                                       where re.ReaderID == readerID
                                       select (int?)re.EventNumber).Max();

                var reader = (from r in context.Readers
                              where r.ReaderID == readerID
                              select new { r.ReaderName, r.ReaderType.ReaderTypeName, r.Webport }).Single();



                Dto.Hello hello = new Hello()
                {
                    LinuxVersion = "1.0.0.0",
                    MacAddress = "00:00:00:00:00:00",
                    NextEventNumber = maxEventNumber.HasValue ? maxEventNumber.Value + 1 : 0,
                    ReaderName = reader.ReaderName,
                    ReaderType = reader.ReaderTypeName,
                    ReaderVersion = "1.0.0.0",
                    Webport = reader.Webport
                };

                return hello;
            }
        }

        public Dto.Status GetStatus(int readerID)
        {
            using (Simulator.Data.SimulatorEntities context = new Simulator.Data.SimulatorEntities())
            {
                var eventCount = (from re in context.ReaderEvents
                                  join r in context.Readers on re.ReaderID equals r.ReaderID
                                  where r.ReaderID == readerID
                                  select re).Count();

                DateTime? earliestEvent = ((from re in context.ReaderEvents
                                            join r in context.Readers on re.ReaderID equals r.ReaderID
                                            where re.ReaderID == readerID
                                            select (DateTime?)re.Timeval).Min());

                DateTime? mostRecentEvent = ((from re in context.ReaderEvents
                                              join r in context.Readers on re.ReaderID equals r.ReaderID
                                              where re.ReaderID == readerID
                                              select (DateTime?)re.Timeval).Max());

                var streamUrl = (from r in context.Readers
                                 where r.ReaderID == readerID
                                 select r.UpstreamUrl).Single();

                return new Status()
                {
                    Count = eventCount,
                    EarliestEvent = earliestEvent.HasValue ? earliestEvent.Value.ToString(TIME_FORMAT) : String.Empty,
                    MostRecentEvent = mostRecentEvent.HasValue ? mostRecentEvent.Value.ToString(TIME_FORMAT) : String.Empty,
                    StreamUrl = streamUrl
                };
            }
        }

        public void UpdateName(int readerID, string name)
        {
            using (Data.SimulatorEntities context = new Data.SimulatorEntities())
            {
                var reader = (from r in context.Readers
                              where r.ReaderID == readerID
                              select r).Single();

                reader.ReaderName = name;

                context.SaveChanges();
            }
        }

        public void UpdateStreamInfo(int readerID, string url, int interval, int maximumEvents)
        {
            using (Data.SimulatorEntities context = new Data.SimulatorEntities())
            {
                var reader = (from r in context.Readers
                              where r.ReaderID == readerID
                              select r).Single();

                if (String.Compare(reader.UpstreamUrl, url, true) != 0 ||
                    reader.EventsInterval != interval ||
                    reader.MaximumEvents != maximumEvents)
                {
                    reader.UpstreamUrl = url;
                    reader.EventsInterval = interval;
                    reader.MaximumEvents = maximumEvents;
                    context.SaveChanges();
                }

            }
        }

        public Dto.TapReaderEvents GetTapReaderEvents(int readerID, string bandID, long sinceEventNumber, int maximumEvents)
        {
            Dto.TapReaderEvents events = new Dto.TapReaderEvents();

            using (Simulator.Data.SimulatorEntities context = new Simulator.Data.SimulatorEntities())
            {
                events.ReaderName =
                (from r in context.Readers
                 where r.ReaderID == readerID
                 select r.ReaderName).Single();
            }

            var result = GetEvents(readerID, bandID, sinceEventNumber, maximumEvents);

            events.ReaderEvents = Mapper.Map<List<Data.GetReaderEvents_Result>, List<Dto.TapReaderEvent>>(result).ToArray();

            return events;
        }

        public Dto.LongRangeReaderEvents GetLongRangeReaderEvents(int readerID, string bandID, long sinceEventNumber, int maximumEvents)
        {
            Dto.LongRangeReaderEvents events = new Dto.LongRangeReaderEvents();

            using (Simulator.Data.SimulatorEntities context = new Simulator.Data.SimulatorEntities())
            {
                events.ReaderName =
                (from r in context.Readers
                 where r.ReaderID == readerID
                 select r.ReaderName).Single();
            }

            var result = GetEvents(readerID, bandID, sinceEventNumber, maximumEvents);

            events.ReaderEvents = Mapper.Map<List<Data.GetReaderEvents_Result>, List<Dto.LongRangeReaderEvent>>(result).ToArray();

            return events;

        }

        public long GetLastUpstreamEvent(int readerID)
        {
            using (Simulator.Data.SimulatorEntities context = new Simulator.Data.SimulatorEntities())
            {
                return (from r in context.Readers
                        where r.ReaderID == readerID
                        select r.LastUpstreamEvent).SingleOrDefault();
            }
        }

        public void SetLastUpstreamEvent(int readerID, long lastUpstreamEvent)
        {
            using (Simulator.Data.SimulatorEntities context = new Simulator.Data.SimulatorEntities())
            {
                var reader = (from r in context.Readers
                        where r.ReaderID == readerID
                        select r).SingleOrDefault();

                reader.LastUpstreamEvent = lastUpstreamEvent;

                context.SaveChanges();
            }
        }

        private List<Data.GetReaderEvents_Result> GetEvents(int readerID, string bandID, long sinceEventNumber, int maximumEvents)
        {
            using (Simulator.Data.SimulatorEntities context = new Simulator.Data.SimulatorEntities())
            {
                return context.GetReaderEvents(readerID, bandID, sinceEventNumber, maximumEvents).ToList();
            }
        }
    }
}
