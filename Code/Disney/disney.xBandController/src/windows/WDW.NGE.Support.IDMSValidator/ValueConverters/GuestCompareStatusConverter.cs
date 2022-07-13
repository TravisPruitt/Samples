using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Data;
using System.Windows.Markup;

namespace WDW.NGE.Support.IDMSValidator.ValueConverters
{
    public class GuestCompareStatusConverter : MarkupExtension, IValueConverter
    {
        public GuestCompareStatusConverter()
        {
        }

        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            List<String> messages = new List<string>();

            if (value != null)
            {
                if (value is Models.GuestCompareStatus)
                {
                    Models.GuestCompareStatus toConvert = (Models.GuestCompareStatus)value;

                    if ((toConvert & Models.GuestCompareStatus.Match) == Models.GuestCompareStatus.Match)
                    {
                        messages.Add("Match");
                    }
                }
            }

            return null;
        }

        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            throw new NotImplementedException();
        }

        public override object ProvideValue(IServiceProvider serviceProvider)
        {
            return instance;
        }

        private static CursorExtensionConverter instance = new CursorExtensionConverter();
    }
}
