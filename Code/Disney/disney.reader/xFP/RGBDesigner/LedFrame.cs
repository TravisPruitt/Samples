using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace RGBDesigner
{
    /// <summary>
    /// Abstract frame class
    /// </summary>
    abstract class Frame
    {
        public abstract Frame copy();

        public abstract bool isDrawFrame();

        public NamedColor this[int index]
        {
            get { return getLed(index); }
            set { setLed(index, value); }
        }

        public string Name
        {
            get { return name(); }
        }

        protected abstract NamedColor getLed(int index);
        protected abstract void setLed(int index, NamedColor color);

        protected abstract string name();
        //
        // Static members
        //

        static public NamedColor OffColor
        {
            get { return offColor; }
        }

        /// <summary>
        /// Used to indicate an LED that is Off in a Frame
        /// </summary>
        static private NamedColor offColor = new NamedColor("Off", 0x00, 0x00, 0x00);
    }


    /// <summary>
    /// Represents a displayed LED frame
    /// </summary>
    class LedFrame : Frame
    {
        public LedFrame()
        {
            for (int i = 0; i < 48; ++i)
            {
                leds.Add(OffColor);
            }
        }

        public override Frame copy()
        {
            LedFrame newFrame = new LedFrame();
            newFrame.displayTime = displayTime;
            for (int i = 0; i < 48; ++i)
            {
                newFrame[i] = this[i];
            }

            return newFrame;
        }

        public override bool isDrawFrame()
        {
            return true;
        }

        protected override NamedColor getLed(int index)
        {
            return leds[index];
        }

        protected override void setLed(int index, NamedColor color)
        {
            leds[index] = color;
        }

        protected override string name()
        {
            return String.Format("Frame ({0})", displayTime);
        }

        public override string ToString()
        {
            string s = displayTime.ToString();
            for (int i = 0; i < leds.Count; ++i)
            {
                if (leds[i] == OffColor)
                {
                    s += ",";
                }
                else
                {
                    s += "," + leds[i].Name;
                }
            }

            return s;
        }

        public double displayTime = 0.0;

        private List<NamedColor> leds = new List<NamedColor>(48);
    }

    /// <summary>
    /// Represent a frame that has no LED effects
    /// </summary>
    abstract class CommandFrame : Frame
    {
        public override bool isDrawFrame()
        {
            return false;
        }

        protected override NamedColor getLed(int index)
        {
            return OffColor;
        }

        protected override void setLed(int index, NamedColor color)
        {
        }
    }

    /// <summary>
    /// Represents a new color command
    /// </summary>
    class NewColorFrame : CommandFrame
    {
        public NewColorFrame(NamedColor color)
        {
            this.color = color;
        }

        public override Frame copy()
        {
            NewColorFrame newFrame = new NewColorFrame(color);
            return newFrame;
        }

        public override string ToString()
        {
            return color.ToString();
        }

        protected override string name()
        {
            return String.Format("Color ({0})", color.Name);
        }

        private NamedColor color;
    }

    /// <summary>
    /// Represents a Repeat command
    /// </summary>
    class RepeatFrame : CommandFrame
    {
        public override Frame copy()
        {
            return new RepeatFrame();
        }

        public override string ToString()
        {
            return "$repeat";
        }

        protected override string name()
        {
            return "Repeat";
        }
    }

    /// <summary>
    /// A container for frame objects with serialization
    /// </summary>
    class LedFrameContainer : List<Frame>
    {
        public void serialize(TextWriter stream)
        {
            for (int i = 0; i < this.Count; ++i)
            {
                stream.WriteLine(this[i].ToString());
            }
        }
    }
}
