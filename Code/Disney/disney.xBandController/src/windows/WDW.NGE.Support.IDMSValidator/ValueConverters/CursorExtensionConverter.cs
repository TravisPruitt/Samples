using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Data;
using System.Windows.Input;
using System.Windows.Markup;

namespace WDW.NGE.Support.IDMSValidator.ValueConverters
{

    public class CursorExtensionConverter : MarkupExtension, IValueConverter
    {
        public CursorExtensionConverter()
        {
        }

        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            if (value != null && ((bool)value))
                return Cursors.Wait;
            else
                return Cursors.Arrow;
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
