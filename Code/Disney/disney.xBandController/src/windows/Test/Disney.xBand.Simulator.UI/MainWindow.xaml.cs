using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.ComponentModel;
using System.Windows.Threading;
using System.Collections.ObjectModel;

namespace Disney.xBand.Simulator.UI
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        Simulation simulation;
        DispatcherTimer dispatcherTimer;

        public MainWindow()
        {
            InitializeComponent();

            dispatcherTimer = new DispatcherTimer();
            dispatcherTimer.Tick += new EventHandler(dispatcherTimer_Tick);
            dispatcherTimer.Interval = new TimeSpan(0, 0, 5);
            dispatcherTimer.Start();

            this.simulation = new Simulation();
        }

        private void startButton_Click(object sender, RoutedEventArgs e)
        {
            if (!this.simulation.InProgress)
            {
                Dto.Attraction attraction = this.attractionComboBox.SelectedItem as Dto.Attraction;
                this.simulation.Start(attraction);
                this.startButton.Content = "Stop";
            }
            else
            {
                this.simulation.Stop();
                this.startButton.Content = "Start";
            }
        }

        //  System.Windows.Threading.DispatcherTimer.Tick handler
        //
        //  Updates the current seconds display and calls
        //  InvalidateRequerySuggested on the CommandManager to force 
        //  the Command to raise the CanExecuteChanged event.
        private void dispatcherTimer_Tick(object sender, EventArgs e)
        {
            if (simulation.InProgress)
            {
                Application.Current.Dispatcher.Invoke(
                    new Action(delegate
                    {
                        Dto.Repositories.IAttractionRepository repository = new Dto.Repositories.AttractionRepository();
                        Dto.Attraction attraction = this.attractionComboBox.SelectedItem as Dto.Attraction;
                        statisticsListView.ItemsSource = repository.GetStatistics(attraction.AttractionID);
                        guestsListView.ItemsSource = repository.GetGuestPositions(attraction.AttractionID);
                        chart.DataSource = repository.GetGuestStates(attraction.AttractionID);
                        chart.DataBind();
                    }));
            }
        }
    }
}
