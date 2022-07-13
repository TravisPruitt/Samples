using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Windows;
using System.Collections.ObjectModel;
using System.Diagnostics;

namespace Disney.xBand.Simulator.UI
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        public ObservableCollection<Dto.Attraction> Attractions { get; private set; }

        private Dto.Repositories.IAttractionRepository attractionRepository;


        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);
            this.attractionRepository = new Dto.Repositories.AttractionRepository();

            this.Attractions = new ObservableCollection<Dto.Attraction>(this.attractionRepository.GetAllActive());
        }
    }
}
