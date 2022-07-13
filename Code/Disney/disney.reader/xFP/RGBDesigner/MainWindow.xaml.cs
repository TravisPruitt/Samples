using System;
using System.IO;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using Microsoft.Win32;
using System.Net;
using System.Windows.Shapes;

namespace RGBDesigner
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
            InitializeColorList();
            InitializeLEDPositions();
            addNewFrame(new LedFrame());
        }

        private LedFrameContainer frames = new LedFrameContainer();

        private int currentFrame
        {
            get { return listViewFrames.SelectedIndex; }
            set
            {
                saveCurrentFrame();
                listViewFrames.SelectedIndex = value;
                drawFrame();
            } 
        }

        private NamedColorList colorList = new NamedColorList();

        private NamedColor currentColor = LedFrame.OffColor;

        //
        // init
        //

        private void InitializeColorList()
        {
            NamedColor c;

            c = Frame.OffColor;
            AddColor(c);

            c = new NamedColor("Red", 0xFF, 0, 0);
            AddColor(c);

            c = new NamedColor("Green", 0, 0xFF, 0);
            AddColor(c);

            c = new NamedColor("Blue", 0, 0, 0xFF);
            AddColor(c);

            c = new NamedColor("White", 0xFF, 0xFF, 0xFF);
            AddColor(c);
        }

        private void InitializeLEDPositions()
        {
            PositionLEDs(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 },
                24, 542f, new Point(0, 0), -82.5f);
            PositionLEDs(new int[] { 32, 33, 34, 35, 36, 37, 38, 39, 40 }, 
                14, 250f, new Point(0, 90), -12f);
            PositionLEDs(new int[] { 47, 24, 25 }, 19, 235f, new Point(0, 90), -109);
        }

        /// <summary>
        /// Poisitions a list of LEDs in a radial pattern.
        /// </summary>
        /// <param name="list">The list of LEDs to position.</param>
        /// <param name="pretend">Pretend there are this many LEDs. For example if you have 6 LEDs that need to be positioned
        /// in a 180 degree arc, pretend there are 12 LEDs to fill in the remaining 180 degrees.</param>
        /// <param name="radius">Radius of the circle.</param>
        /// <param name="origin">The X and Y center of the circle.</param>
        /// <param name="offset">Angle offset (angle at which the first LED with be drawn).</param>
        private void PositionLEDs(int[] list, int pretend, float radius, Point origin, float offset)
        {
            Size s = new Size(image1.Source.Width, image1.Source.Height);
            for (int i = 0; i < list.Length; i++)
            {
                double angle = (((360f / pretend) * i) + offset) * (Math.PI / 180);
                Point point = new Point(
                    (int)Math.Round(radius * Math.Cos(angle), 0),
                    (int)Math.Round(radius * Math.Sin(angle), 0));
                point.X += origin.X;
                point.Y += origin.Y;
                Button btn = findLedButton(list[i]);
                btn.HorizontalAlignment = System.Windows.HorizontalAlignment.Center;
                btn.VerticalAlignment = System.Windows.VerticalAlignment.Center;
                btn.Margin = new Thickness(point.X, point.Y, 0, 0);
            }
        }

        //
        // Color stuff
        //

        private void AddColor(NamedColor c)
        {
            colorList.Add(c);

            StackPanel p = new StackPanel();
            p.Orientation = Orientation.Horizontal;

            Rectangle r = new Rectangle();
            r.Margin = new Thickness(0, 1, 0, 0);
            r.Width = r.Height = 16;
            r.Fill = new SolidColorBrush(c.Color);
            r.Stroke = Brushes.Black;

            TextBlock t = new TextBlock();
            t.Text = c.Name;
            t.Margin = new Thickness(5, 0, 0, 0);

            p.Children.Add(r);
            p.Children.Add(t);

            listViewColors.Items.Add(new ListViewItem() { Content = p });
            addNewFrame(new NewColorFrame(c));
        }

        private void EditColor(int index, NamedColor nc)
        {
            colorList[index].Color = nc.Color;
            colorList[index].Name = nc.Name;
            StackPanel p = (StackPanel)((ListViewItem)listViewColors.Items[index]).Content;
            Rectangle r = (Rectangle)p.Children[0];
            r.Fill = new SolidColorBrush(nc.Color);
            TextBlock t = (TextBlock)p.Children[1];
            t.Text = nc.Name;
            drawFrame();
        }

        private void AddAllHues()
        {
            for (int i = 0; i < 360; i++)
            {
                System.Drawing.Color c = CPicker.HSVToRGB((float)i, 1.0, 1.0);
                var nc = new NamedColor(string.Format("h{0}", i.ToString()), c);
                AddColor(nc);
            }
        }


        //
        // Frame manipulation
        //

        private void addNewFrame(Frame frame)
        {
            // Add frame to frames list
            var i = new ListViewItem();
            i.Content = frame.Name;
            listViewFrames.Items.Add(i);

            // Add frame to container and move to it
            frames.Add(frame);
            currentFrame = frames.Count - 1;
        }

        private void saveCurrentFrame()
        {
            if (currentFrame < 0)
                return;

            if (frames[currentFrame] is LedFrame)
                ((LedFrame)frames[currentFrame]).displayTime = Convert.ToDouble(numDisplayTime.Value);
        }

        private void drawFrame()
        {
            if (currentFrame < 0)
                return;

            Frame frame = frames[currentFrame];

            for (int i = 0; i < 48; ++i)
            {
                findLedButton(i).Background = new SolidColorBrush(frame[i].Color);
            }

            if (frame is LedFrame)
                numDisplayTime.Value = Convert.ToDecimal(((LedFrame)frame).displayTime);
            else
                numDisplayTime.Value = 0;

            updateFrameSelector();
        }

        private void updateFrameSelector()
        {
            for (int i = 0; i < frames.Count; ++i)
            {
                ((ListViewItem)listViewFrames.Items[i]).Content = frames[i].Name;
            }
        }

        private Button findLedButton(int ledNumber)
        {
            Button btn = null;

            for (int i = 0; i < ledPanel.Children.Count; ++i)
            {
                if (ledPanel.Children[i].GetType() == typeof(Button))
                {
                    btn = (Button)ledPanel.Children[i];
                    if (int.Parse((string)btn.Content) == ledNumber)
                        break;
                }
            };

            return btn;
        }

        private void GenerateRandomFrame()
        {
            Random r = new Random(Environment.TickCount);
            for (int k = 0; k < 48; ++k)
            {
                frames[currentFrame][k] = colorList[r.Next(0, colorList.Count)];
            };
            drawFrame();
        }

        private void rotateRangeLeft(int start, int stop)
        {
            if (!frames[currentFrame].isDrawFrame())
                return;

            LedFrame frame = (LedFrame)frames[currentFrame];
            NamedColor tmp = frame[start];
            for (int i = start; i < stop; ++i)
            {
                frame[i] = frame[i + 1];
            }
            frame[stop] = tmp;
            drawFrame();
        }

        private void rotateRangeRight(int start, int stop)
        {
            if (!frames[currentFrame].isDrawFrame())
                return;

            LedFrame frame = (LedFrame)frames[currentFrame];
            NamedColor tmp = frame[stop];
            for (int i = stop; i > start; --i)
            {
                frame[i] = frame[i - 1];
            }
            frame[start] = tmp;
            drawFrame();
        }

        //
        // UI event handlers
        //

        private void buttonLED_Click(object sender, RoutedEventArgs e)
        {
            if (currentColor != null)
            {
                Button b = (Button)sender;
                int index = Int32.Parse(b.Content.ToString());
                frames[currentFrame][index] = currentColor;
                drawFrame();
            }
        }

        private void listViewColors_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            int index = listViewColors.SelectedIndex;
            if (index < 0 || index >= colorList.Count)
                currentColor = null;
            else
                currentColor = colorList[index];
        }

        private void buttonNewColor_Click(object sender, RoutedEventArgs e)
        {
            ColorPicker.AllowAlpha = false;
            if (ColorPicker.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                var c = new NamedColor(ColorPicker.ColorName, ColorPicker.Color);
                AddColor(c);
            }
        }

        private void buttonAddKnownColors_Click(object sender, RoutedEventArgs e)
        {
            string[] colors = Enum.GetNames(typeof(System.Drawing.KnownColor));
            for (int i = 0; i < colors.Length; i++)
            {
                var c = System.Drawing.Color.FromKnownColor((System.Drawing.KnownColor)Enum.Parse(typeof(System.Drawing.KnownColor), colors[i]));
                NamedColor nc = new NamedColor(colors[i], c);
                AddColor(nc);
            }
        }

        private void buttonDeleteColor_Click(object sender, RoutedEventArgs e)
        {
            if (listViewColors.SelectedIndex >= 0)
            {
                int index = listViewColors.SelectedIndex;
                colorList.RemoveAt(index);
                listViewColors.Items.RemoveAt(index);
                listViewColors.SelectedIndex = Math.Min(index, listViewColors.Items.Count - 1);
            }
        }

        private void buttonEditColor_Click(object sender, RoutedEventArgs e)
        {
            int index = listViewColors.SelectedIndex;
            NamedColor selected = colorList[index];
            ColorPicker.Color = System.Drawing.Color.FromArgb(selected.Color.A, selected.Color.R, selected.Color.G, selected.Color.B);
            ColorPicker.ColorName = selected.Name;
            if (ColorPicker.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                NamedColor nc = new NamedColor(ColorPicker.ColorName, ColorPicker.Color);
                EditColor(index, nc);
            }
        }

        private void btnSetAll_Click(object sender, RoutedEventArgs e)
        {
            for (int i = 0; i < 48; ++i)
            {
                frames[currentFrame][i] = currentColor;
            };
            drawFrame();
        }

        private void btnSetInner_Click(object sender, RoutedEventArgs e)
        {
            for (int i = 24; i < 48; ++i)
            {
                frames[currentFrame][i] = currentColor;
            };
            drawFrame();
        }

        private void btnSetOuter_Click(object sender, RoutedEventArgs e)
        {
            for (int i = 0; i < 24; ++i)
            {
                frames[currentFrame][i] = currentColor;
            };
            drawFrame();
        }

        private void btnRandomize_Click(object sender, RoutedEventArgs e)
        {
            GenerateRandomFrame();
        }

        private void buttonRotateLeft_Click(object sender, RoutedEventArgs e)
        {
            rotateRangeLeft(0, 47);
        }

        private void buttonRotateRight_Click(object sender, RoutedEventArgs e)
        {
            rotateRangeRight(0, 47);
        }

        private void buttonCopyRotateLeft_Click(object sender, RoutedEventArgs e)
        {
            if (!(frames[currentFrame] is LedFrame))
                return;
            addNewFrame(frames[currentFrame].copy());
            rotateRangeLeft(0, 47);
        }

        private void buttonCopyRotateRight_Click(object sender, RoutedEventArgs e)
        {
            if (!(frames[currentFrame] is LedFrame))
                return;
            addNewFrame(frames[currentFrame].copy());
            rotateRangeRight(0, 47);
        }

        private void buttonRotateInnerLeft_Click(object sender, RoutedEventArgs e)
        {
            rotateRangeLeft(24, 47);
        }

        private void buttonRotateInnerRight_Click(object sender, RoutedEventArgs e)
        {
            rotateRangeRight(24, 47);
        }

        private void buttonCopyRotateInnerLeft_Click(object sender, RoutedEventArgs e)
        {
            if (!(frames[currentFrame] is LedFrame))
                return;
            addNewFrame(frames[currentFrame].copy());
            rotateRangeLeft(24, 47);
        }

        private void buttonCopyRotateInnerRight_Click(object sender, RoutedEventArgs e)
        {
            if (!(frames[currentFrame] is LedFrame))
                return;
            addNewFrame(frames[currentFrame].copy());
            rotateRangeRight(24, 47);
        }

        private void buttonRotateOuterLeft_Click(object sender, RoutedEventArgs e)
        {
            rotateRangeLeft(0, 23);
        }

        private void buttonRotateOuterRight_Click(object sender, RoutedEventArgs e)
        {
            rotateRangeRight(0, 23);
        }

        private void buttonCopyRotateOuterLeft_Click(object sender, RoutedEventArgs e)
        {
            if (!(frames[currentFrame] is LedFrame))
                return;
            addNewFrame(frames[currentFrame].copy());
            rotateRangeLeft(0, 23);
        }

        private void buttonCopyRotateOuterRight_Click(object sender, RoutedEventArgs e)
        {
            if (!(frames[currentFrame] is LedFrame))
                return;
            addNewFrame(frames[currentFrame].copy());
            rotateRangeRight(0, 23);
        }

        private void buttonAddFrame_Click(object sender, RoutedEventArgs e)
        {
            addNewFrame(new LedFrame());
        }

        private void buttonCopyFrame_Click(object sender, RoutedEventArgs e)
        {
            if (frames[currentFrame] is LedFrame)
                addNewFrame(frames[currentFrame].copy());
        }

        private void btnRepeat_Click(object sender, RoutedEventArgs e)
        {
            addNewFrame(new RepeatFrame());
        }

        private void buttonSave_Click(object sender, RoutedEventArgs e)
        {
            SaveFileDialog d = new SaveFileDialog();
            if (!d.ShowDialog().Value)
                return;

            saveCurrentFrame();

            StreamWriter s = new StreamWriter(d.OpenFile());

            s.WriteLine("#,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47");
            
            // TODO: remove Temp hack to use relative time
            s.WriteLine("$timemode_duration");
            
            frames.serialize(s);

            s.Close();
        }

        private void buttonSend_Click(object sender, RoutedEventArgs e)
        {
            saveCurrentFrame();

            StringWriter s = new StringWriter();

            // TODO: remove Temp hack to use relative time
            s.WriteLine("$timemode_duration");

            frames.serialize(s);

            try
            {
                string url = "http://" + textBoxReaderIp.Text;
                if (textBoxReaderPort.Text != String.Empty)
                    url += ":" + textBoxReaderPort.Text + "/media/ledscript";

                var request = (HttpWebRequest)WebRequest.Create(url);
                request.Method = "POST";
                request.KeepAlive = false;
                request.Timeout = 5000;
                request.ReadWriteTimeout = 5000;

                using (StreamWriter ss = new StreamWriter(request.GetRequestStream()))
                {
                    ss.Write(s.ToString());
                }
                var response = (HttpWebResponse)request.GetResponse();
                response.Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        private void listViewFrames_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            // Don't allow no selection
            if (listViewFrames.SelectedIndex < 0)
                listViewFrames.SelectedIndex = currentFrame;

            //currentFrame = listViewFrames.SelectedIndex;
            drawFrame();
        }

        private void numDisplayTime_Click(object sender, EventArgs e)
        {
            saveCurrentFrame();
            updateFrameSelector();
        }

        private void buttonDeleteFrame_Click(object sender, RoutedEventArgs e)
        {
            if (listViewFrames.SelectedIndex >= 0)
            {
                
                if (frames.Count == 1)
                {
                    MessageBox.Show("Cannot delete all frames", "Warning", MessageBoxButton.OK, MessageBoxImage.Exclamation);
                    return;
                }

                int oldFrame = currentFrame;
                if (oldFrame == 0)
                {
                    currentFrame = oldFrame + 1;
                }
                else
                {
                    currentFrame = oldFrame - 1;
                }

                frames.RemoveAt(oldFrame);
                listViewFrames.Items.RemoveAt(oldFrame);
            }
        }

        private void buttonMoveFrameDown_Click(object sender, RoutedEventArgs e)
        {
            if (currentFrame >= (listViewFrames.Items.Count - 1))
                return;

            // First move the frame object
            object tmp = frames[currentFrame];
            frames[currentFrame] = frames[currentFrame + 1];
            frames[currentFrame + 1] = (Frame)tmp;
            
            // Next move the listview item down
            tmp = listViewFrames.Items[currentFrame + 1];
            listViewFrames.Items.RemoveAt(currentFrame + 1);
            listViewFrames.Items.Insert(currentFrame, tmp);
        }

        private void buttonMoveFrameUp_Click(object sender, RoutedEventArgs e)
        {
            if (listViewFrames.SelectedIndex == 0)
                return;

            // First move the frame object
            object tmp = frames[currentFrame];
            frames[currentFrame] = frames[currentFrame - 1];
            frames[currentFrame - 1] = (Frame)tmp;

            // Next move the listview item up
            tmp = listViewFrames.Items[currentFrame - 1];
            listViewFrames.Items.RemoveAt(currentFrame - 1);
            listViewFrames.Items.Insert(currentFrame + 1, tmp);
        }
    }
}
