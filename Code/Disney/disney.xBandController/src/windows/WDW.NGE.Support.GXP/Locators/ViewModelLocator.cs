/*
  In App.xaml:
  <Application.Resources>
      <vm:ViewModelLocator xmlns:vm="clr-namespace:WDW.NGE.GXP.Support"
                                   x:Key="Locator" />
  </Application.Resources>
  
  In the View:
  DataContext="{Binding Source={StaticResource Locator}, Path=ViewModelName}"
*/

using System;
using System.Linq;
using System.Collections.Generic;
using System.Collections.ObjectModel;

// Toolkit namespace
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.GXP.Locators
{
    /// <summary>
    /// This class creates ViewModels on demand for Views, supplying a
    /// ServiceAgent to the ViewModel if required.
    /// <para>
    /// Place the ViewModelLocator in the App.xaml resources:
    /// </para>
    /// <code>
    /// &lt;Application.Resources&gt;
    ///     &lt;vm:ViewModelLocator xmlns:vm="clr-namespace:WDW.NGE.GXP.Support"
    ///                                  x:Key="Locator" /&gt;
    /// &lt;/Application.Resources&gt;
    /// </code>
    /// <para>
    /// Then use:
    /// </para>
    /// <code>
    /// DataContext="{Binding Source={StaticResource Locator}, Path=ViewModelName}"
    /// </code>
    /// <para>
    /// Use the <strong>mvvmlocator</strong> or <strong>mvvmlocatornosa</strong>
    /// code snippets to add ViewModels to this locator.
    /// </para>
    /// </summary>
    public class ViewModelLocator
    {
        // Create MainPageViewModel on demand
        public ViewModels.MainPageViewModel MainPageViewModel
        {
            get { return new ViewModels.MainPageViewModel(); }
        }

        public ViewModels.IndividualEligibilityViewModel IndividualEligiblityViewModel
        {
            get { return new ViewModels.IndividualEligibilityViewModel(); }
        }

        public ViewModels.GroupEligibilityViewModel GroupEligiblityViewModel
        {
            get { return new ViewModels.GroupEligibilityViewModel(); }
        }

        public ViewModels.EntitlementViewModel EntitlementViewModel
        {
            get { return new ViewModels.EntitlementViewModel(); }
        }

        public ViewModels.XbandRequestDetailsViewModel XbandDetailsViewModel
        {
            get { return new ViewModels.XbandRequestDetailsViewModel(); }
        }

        public ViewModels.GuestEligibilityViewModel GuestEligibilityViewModel 
        {
            get { return new ViewModels.GuestEligibilityViewModel(); }
        }
    }
}