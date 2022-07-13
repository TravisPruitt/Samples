using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.GXP.Views
{
    /// <summary>
    /// Interaction logic for EntitlementView.xaml
    /// </summary>
    public partial class EntitlementView : UserControl
    {
        public EntitlementView()
        {
            InitializeComponent();
        
            // Get model from data context
            var vm = (ViewModels.EntitlementViewModel)DataContext;

            // Subscribe to notifications from the model
            vm.ErrorNotice += OnErrorNotice;
        }
    
        void OnErrorNotice(object sender, NotificationEventArgs<Exception> e)
        {
            MessageBox.Show(e.Data.Message, "Error", MessageBoxButton.OK);
        }
    }
}
