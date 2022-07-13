using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AutoMapper;
using System.Diagnostics;
using System.Data.Objects;
using System.Net;
using System.Runtime.Serialization.Json;
using System.IO;

namespace Disney.xBand.Simulator.Dto.Repositories
{
    public interface IGuestRepository
    {
        List<Dto.Guest> Initialize(Dto.Attraction attraction);
        void UpdateGuestPosition(Dto.Guest guest, int attractionID);
    }

    public class GuestRepository : IGuestRepository
    {
        //private long totalRequestMilliseconds;

        static GuestRepository()
        {
            Mapper.Initialize(cfg =>
            {
                cfg.CreateMap<Data.MagicBand, Dto.MagicBand>()
                    .ForMember(dest => dest.Channel, opts => opts.Ignore())
                    .ForMember(dest => dest.Frequency, opts => opts.Ignore());
                cfg.CreateMap<Data.Guest, Dto.Guest>()
                    .ForMember(dest => dest.GuestState, opts => opts.Ignore())
                    .ForMember(dest => dest.HasFastPassPlus, opts => opts.Ignore())
                    .ForMember(dest => dest.SequenceNumber, opts => opts.Ignore())
                    .ForMember(dest => dest.xPosition, opts => opts.Ignore())
                    .ForMember(dest => dest.yPosition, opts => opts.Ignore())
                    .ForMember(dest => dest.EntryTime, opts => opts.Ignore())
                    .ForMember(dest => dest.LoadTime, opts => opts.Ignore())
                    .ForMember(dest => dest.ExitTime, opts => opts.Ignore());
            });

            Mapper.AssertConfigurationIsValid();
        }

        public GuestRepository()
        {
        }

        public List<Dto.Guest> Initialize(Dto.Attraction attraction)
        {
            List<Dto.Guest> guestQueue = new List<Dto.Guest>();

            try
            {
                Stopwatch sw = new Stopwatch();
                sw.Start();

                using (Data.SimulatorEntities context = new Data.SimulatorEntities())
                {
                    var result = context.GetGuests().ToList();

                    guestQueue = Mapper.Map<List<Data.Guest>, List<Dto.Guest>>(result);

                    int sequenceNumber = 1;
                    foreach (Dto.Guest guest in guestQueue)
                    {
                        guest.SequenceNumber = sequenceNumber;
                        sequenceNumber++;
                    }

                    Debug.WriteLine(
                        String.Format("Initializing guests took {0} milliseconds.", sw.ElapsedMilliseconds));
                    sw.Restart();

                }
            }
            catch (Exception ex)
            {
                Debug.WriteLine(String.Format("{0}{1}{2}", ex.Message, Environment.NewLine, ex.StackTrace));
            }

            return guestQueue;

        }


        public void UpdateGuestPosition(Dto.Guest guest, int attractionID)
        {
            if (guest.GuestState != GuestState.Indeterminate &&
                guest.GuestState != GuestState.Arriving)
            {
                using (Data.SimulatorEntities context = new Data.SimulatorEntities())
                {
                    context.UpdateGuestPosition(guest.GuestID,
                        attractionID,
                        guest.EntryTime,
                        guest.HasFastPassPlus,
                        (int)guest.GuestState,
                        guest.xPosition,
                        guest.yPosition,
                        guest.LoadTime,
                        guest.ExitTime);
                }
            }
        }
    }
}
