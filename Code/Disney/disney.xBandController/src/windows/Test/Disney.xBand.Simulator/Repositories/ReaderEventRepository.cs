using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AutoMapper;

namespace Disney.xBand.Simulator.Repositories
{
    public interface IReaderEventRepository
    {
        void WriteEvent(Dto.ReaderEvent readerEvent);
        void WriteEvents(List<Dto.ReaderEvent> readerEvents);

    }

    public class ReaderEventRepository : IReaderEventRepository
    {
       static ReaderEventRepository()
        {
            Mapper.Initialize(cfg =>
            {
                cfg.CreateMap<List<Dto.ReaderEvent>, List<Data.ReaderEvent>>();
                cfg.CreateMap<Data.ReaderEvent, Dto.ReaderEvent>();
                cfg.CreateMap<List<Dto.ReaderEvent>, List<Data.ReaderEvent>>();
                cfg.CreateMap<List<Data.ReaderEvent>, List<Dto.ReaderEvent>>();
            });
        }
        public void WriteEvent(Dto.ReaderEvent readerEvent)
        {
            using (Data.SimulatorEntities context = new Data.SimulatorEntities())
            {
                context.WriteReaderEvent(readerEvent.ReaderID, 
                    readerEvent.BandID, 
                    readerEvent.SignalStrength, 
                    readerEvent.Channel, 
                    readerEvent.PacketSequence, 
                    readerEvent.Frequency);
            }
        }

        public void WriteEvents(List<Dto.ReaderEvent> readerEvents)
        {
            using (Data.SimulatorEntities context = new Data.SimulatorEntities())
            {
                foreach (Dto.ReaderEvent readerEvent in readerEvents)
                {
                    context.WriteReaderEvent(readerEvent.ReaderID,
                       readerEvent.BandID,
                       readerEvent.SignalStrength,
                       readerEvent.Channel,
                       readerEvent.PacketSequence,
                       readerEvent.Frequency);
                }
            }
        }
    }
}
