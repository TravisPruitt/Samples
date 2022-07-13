using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Disney.xBand.Simulator.Data;
using AutoMapper;
using System.Diagnostics;
using System.Data.Objects;

namespace Disney.xBand.Simulator.Repositories
{
    public interface IGuestRepository
    {
        void Initialize(int attractionID);
        void UpdateGuests(int attractionID, ref DateTime lastLoad, int guestsToLoad, DateTime simulationTime);
        void CreateReaderEvents(int attractionID, DateTime simulationTime);
        void ResetMagicBands(DateTime simulationTime);
    }

    public class GuestRepository : IGuestRepository
    {
        static GuestRepository()
        {
            Mapper.Initialize(cfg =>
            {
                cfg.ForSourceType<Data.GuestQueue>().AllowNullDestinationValues = true;
                cfg.CreateMap<List<Data.GuestQueue>, List<Dto.GuestQueueItem>>();
                cfg.CreateMap<List<Data.MagicBand>, List<Dto.MagicBand>>();
                cfg.CreateMap<Data.GuestQueue, Dto.GuestQueueItem>()
                    .ForMember(dest => dest.Attraction, opt => opt.Ignore());
                cfg.CreateMap<Data.MagicBand, Dto.MagicBand>();
                cfg.CreateMap<Data.Guest, Dto.Guest>();
            });
        }

        public GuestRepository()
        {
        }

        public void Initialize(int attractionID)
        {
            Stopwatch sw = new Stopwatch();
            sw.Start();

            using (Data.SimulatorEntities context = new Data.SimulatorEntities())
            {
                context.InitializeGuestQueue(attractionID);



                //using (Data.IdmsEntities idmsContext = new Data.IdmsEntities())
                //{
                //    List<Data.guest> guests = (from g in idmsContext.guests
                //                               where g.createdBy == "simulator"
                //                               select g).ToList();

                //    foreach (Data.guest guest in guests)
                //    {
                //        context.AddGuest(guest.guestId, guest.lastName, guest.firstName);

                //        foreach (Data.guest_xband guest_xband in guest.guest_xband)
                //        {
                //            context.AddMagicBand(
                //                guest_xband.guestId,
                //                guest_xband.xbandId,
                //                guest_xband.xband.bandId,
                //                guest_xband.xband.tapId,
                //                guest_xband.xband.longRangeId);
                //        }
                //    }
                //}
            }

            Debug.WriteLine(
                String.Format("Initializing guests took {0} milliseconds.",
                sw.ElapsedMilliseconds));
        }


        public void UpdateGuests(int attractionID, ref DateTime lastLoad, int guestsToLoad, DateTime simulationTime)
        {
            using (Data.SimulatorEntities context = new Data.SimulatorEntities())
            {
                context.UpdateOutOfRange(attractionID, simulationTime);

                context.UpdateExited(attractionID, simulationTime);

                if (lastLoad.AddSeconds(30) > DateTime.Now)
                {
                    ObjectResult<int?> result = context.UpdateRiding(attractionID, simulationTime);

                    guestsToLoad = result.Select(r => r.Value).Single();

                    context.UpdateLoading(attractionID, guestsToLoad, simulationTime);

                    context.UpdateMerging(attractionID, guestsToLoad, simulationTime);

                    lastLoad = DateTime.Now;
                }

                context.UpdateInQueue(attractionID, simulationTime);

                context.UpdateEntered(attractionID, simulationTime);
            }

        }


        //public List<Dto.GuestQueueItem> GetGuestsInQueue(int attractionID)
        //{
        //    using (Data.SimulatorEntities context = new SimulatorEntities())
        //    {
        //        var guests = context.GetGuestsInQueue(attractionID).ToList();

        //        return Mapper.Map<List<Data.GuestQueue>, List<Dto.GuestQueueItem>>(guests);
        //    }
        //}

        public void ResetMagicBands(DateTime simluationTime)
        {
            using (Data.SimulatorEntities context = new SimulatorEntities())
            {
                context.ResetMagicBands(simluationTime);
            }
        }

        public void CreateReaderEvents(int attractionID, DateTime simulationTime)
        {
            using (Data.SimulatorEntities context = new SimulatorEntities())
            {
                context.CreateReaderEvents(attractionID, simulationTime);
            }
        }
    }
}
