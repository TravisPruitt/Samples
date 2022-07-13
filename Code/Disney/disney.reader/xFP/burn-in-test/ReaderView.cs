using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Drawing;
using System.Windows.Forms;
using System.ComponentModel;



namespace xTPManufacturerTest
{
    public class ReaderView
    {
        public static Bitmap progressImage = xTPManufacturerTest.Properties.Resources.white_off_32;
        public static Bitmap successImage = xTPManufacturerTest.Properties.Resources.green_on_32;
        public static Bitmap errorImage = xTPManufacturerTest.Properties.Resources.red_on_32;

        private Reader reader;

        public ReaderView(Reader reader)
        {
            this.reader = reader;
        }

        public Reader getReader()
        {
            return reader;
        }

        public Image StatusIndicator
        {
            get
            {
                if (reader.okay)
                    return successImage;
                else
                    return errorImage;
            }
        }

        public string Mac
        {
            get 
            { 
                return reader.Mac; 
            }
        }

        public string IPAddress
        {
            get { return reader.IPAddress; }
        }

        public string Type
        {
            get { return reader.type; }
        }
    }



    public class ReaderList : BindingList<ReaderView>
    {
        public Reader findMac(string mac)
        {
            for (int index = 0; index < this.Count; ++index)
            {
                if (this[index].Mac != null && this[index].Mac.Equals(mac))
                    return this[index].getReader();
            }
            // didn't find it
            return null;
        }

        public Reader getReader(int index)
        {
            if (index < this.Count)
                return this[index].getReader();
            else
                return null;
        }
    }

}
