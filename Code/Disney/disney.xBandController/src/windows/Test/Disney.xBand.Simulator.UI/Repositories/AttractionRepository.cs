using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;

namespace Disney.xBand.Simulator.UI.Repositories
{
    public interface IAttractionRepository
    {
        ObservableCollection<Data.Attraction> GetAllActive();

    }

    public class AttractionRepository : IAttractionRepository
    {
        public ObservableCollection<Data.Attraction> GetAllActive()
        {
            using (Data.SimulatorEntities context = new Data.SimulatorEntities())
            {
                var result = (from a in context.Attractions
                                  .Include("Controller")
                                  .Include("Controller.Readers.ReaderLocationType")
                                  .Include("Controller.Readers.ReaderType")
                                  .Include("Controller.Readers")
                              where a.IsActive == true
                              select a)
                              .ToList();

                return new ObservableCollection<Data.Attraction>(result);
            }
        }
    }
}
