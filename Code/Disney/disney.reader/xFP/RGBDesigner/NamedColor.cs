using System;
using System.Collections.Generic;
using System.Windows.Media;
using System.IO;

namespace RGBDesigner
{
    /// <summary>
    /// Represents a color that is associated with a string name
    /// </summary>
    class NamedColor
    {
        public NamedColor(string name, byte r, byte g, byte b)
        {
            Name = name;
            Color = Color.FromRgb(r, g, b);
        }

        public NamedColor(string name, System.Drawing.Color color)
        {
            Name = name;
            Color = Color.FromRgb(color.R, color.G, color.B);
        }

        public string Name
        {
            get;
            set;
        }

        public Color Color
        {
            get;
            set;
        }

        override public string ToString()
        {
            return String.Format("$color {0} {1} {2} {3}" + string.Empty.PadLeft(48, ','),
                Name, Color.R, Color.G, Color.B);
        }
    }

    /// <summary>
    /// 
    /// </summary>
    class NamedColorList : List<NamedColor>
    {
        public void serialize(TextWriter stream)
        {
            // Skip the first color
            for (int i = 1; i < this.Count; ++i)
            {
                stream.WriteLine(this[i].ToString());
            }
        }
    }
}
