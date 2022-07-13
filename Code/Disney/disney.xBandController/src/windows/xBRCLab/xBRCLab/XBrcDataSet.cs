using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using System.IO;
using System.Xml;
using System.Xml.Serialization;
using System.Globalization;

namespace com.disney.xband.xbrc.xBRCLab
{
    [XmlRoot("xbrcdataset")]
    public class XBrcDataSet: IXmlSerializable
    {
        // public enum
        public enum DataSetType
        {
            Raw,
            Spatial
        };

        // class data
        private string sName;
        private string sDescription;
        private DataSetType dsType = DataSetType.Raw;
        private DateTime dtCreated;
        private double dblFeigSpacing = 2.0;               // 2 meter default

        private long lRecord = 0;
        private DataTable dtLrr = null;
        private DataTable dtTap = null;
        private DataTable dtSing = null;
        private DateTime dtEarliest = DateTime.MaxValue;
        private DateTime dtLatest = DateTime.MinValue;

        public XBrcDataSet()
        {
            initTables();
        }

        public XBrcDataSet(string sName, string sDescription, DataSetType dst) : this()
        {
            this.sName = sName;
            this.sDescription = sDescription;
            this.dsType = dst;
            this.dtCreated = DateTime.UtcNow;
        }

        public XBrcDataSet(string sName, string sDescription, DataSetType dst, double dblFeigSpacing) : this(sName, sDescription, dst)
        {
            this.dblFeigSpacing = dblFeigSpacing;
        }

        public string getName()
        {
            return sName;
        }

        public string getDescription()
        {
            return sDescription;
        }

        public DateTime getDate()
        {
            return dtCreated;
        }

        public DataSetType getDataSetType()
        {
            return dsType;
        }

        public double getFeigSpacing()
        {
            if (dsType != DataSetType.Spatial)
                throw new Exception("Cannot retrieve Feig spacing for a Raw dataset");

            return dblFeigSpacing;
        }

        public DateTime getEarliestTime()
        {
            return dtEarliest;
        }

        public DateTime getLatestTime()
        {
            return dtLatest;
        }

        public void store(string sDataDir)
        {
            string sFile = Path.Combine(sDataDir, sName + ".xml");
            StreamWriter sw = new StreamWriter(sFile);
            XmlSerializer s = new XmlSerializer(typeof(XBrcDataSet));
            s.Serialize(sw, this);
            sw.Close();
        }

        public static XBrcDataSet loadXML(string sFilename)
        {
            StreamReader sr = new StreamReader(sFilename);
            XmlSerializer s = new XmlSerializer(typeof(XBrcDataSet));
            XBrcDataSet ds = s.Deserialize(sr) as XBrcDataSet;
            sr.Close();

            return ds;
        }

        public static bool isDataSet(string sFile, out string sName, out DateTime dt, out string sType, out string sDescription)
        {
            // initialize
            sName = null;
            dt = DateTime.MinValue;
            sType = null;
            sDescription = null;

            XmlReaderSettings settings = new XmlReaderSettings();
            settings.ConformanceLevel = ConformanceLevel.Fragment;
            settings.IgnoreComments = true;
            settings.IgnoreWhitespace = true;
            XmlReader rdr = XmlReader.Create(sFile, settings);
            rdr.Read();
            if (rdr.Name == "xml")
                rdr.Read();
            if (rdr.Name != "xbrcdataset")
            {
                rdr.Close();
                return false;
            }

            // now get the description element
            if (!rdr.ReadToDescendant("description"))
            {
                rdr.Close();
                return false;
            }

            // get the attributes
            sName = Path.GetFileNameWithoutExtension(sFile);
            sType = rdr.GetAttribute("type");
            string sDate = rdr.GetAttribute("date");
            dt = parseDate(sDate);
            if (sType == null || sDate == null || dt==DateTime.MinValue)
            {
                rdr.Close();
                return false;
            }
            sDescription = rdr.ReadElementString();
            rdr.Close();

            return true;
        }

        public DataTable getLrrTable()
        {
            return dtLrr;
        }

        public DataTable getTapTable()
        {
            return dtTap;
        }

        public DataTable getSingulationTable()
        {
            return dtSing;
        }

        private void initTables()
        {
            dtLrr = new DataTable();
            dtLrr.TableName = "lrr";
            dtLrr.Columns.Add("Record", typeof(int));
            dtLrr.Columns.Add("Timestamp", typeof(DateTime));
            dtLrr.Columns.Add("Reader", typeof(string));
            dtLrr.Columns.Add("Guest", typeof(string));
            dtLrr.Columns.Add("LRID", typeof(string));
            dtLrr.Columns.Add("Pno", typeof(int));
            dtLrr.Columns.Add("SS", typeof(int));
            dtLrr.Columns.Add("Freq", typeof(int));
            dtLrr.Columns.Add("Channel", typeof(int));
            dtLrr.DefaultView.Sort = "Timestamp ASC, LRID ASC, Pno ASC, Reader ASC, Channel ASC";

            dtTap = new DataTable();
            dtTap.TableName = "tap";
            dtTap.Columns.Add("Record", typeof(int));
            dtTap.Columns.Add("Timestamp", typeof(DateTime));
            dtTap.Columns.Add("Reader", typeof(string));
            dtTap.Columns.Add("Guest", typeof(string));
            dtTap.Columns.Add("RFID", typeof(string));
            dtTap.DefaultView.Sort = "Timestamp ASC, RFID ASC, Reader ASC";

            dtSing = new DataTable();
            dtSing.TableName = "singulation";
            dtSing.Columns.Add("Record", typeof(int));
            dtSing.Columns.Add("Timestamp", typeof(DateTime));
            dtSing.Columns.Add("Location", typeof(string));
            dtSing.Columns.Add("Guest", typeof(string));
            dtSing.Columns.Add("Basis", typeof(string));

        }

        public void addRecords(string[] aLines)
        {
            foreach (string sLine in aLines)
            {
                // crack the fields
                string[] aParts = sLine.Split(new char[] { ',' });

                if (aParts.Length < 3)
                    continue;               // ignore bad lines

                // handle various types
                string sRecType = aParts[1];
                switch (sRecType)
                {
                    case "LRR":
                        {
                            handleLrrRecord(aParts);
                            break;
                        }

                    case "TAP":
                        {
                            handleTapRecords(aParts);
                            break;
                        }

                    case "SNG":
                        {
                            handleSngRecords(aParts);
                            break;
                        }

                    default:
                        // ignore
                        break;
                }
            }

        }

        private void handleSngRecords(string[] aParts)
        {
            if (aParts.Length!=5)
                throw new Exception("Invalid SNG data!");

            long lTIme;
            if (!long.TryParse(aParts[0], out lTIme))
                throw new Exception("Invalid SNG data timestamp!");

            // calculate UTC time
            DateTime dtEpoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
            DateTime dt = dtEpoch + TimeSpan.FromMilliseconds(lTIme);
            string sReader = aParts[2];
            string sGuest = aParts[3];
            string sBasis = aParts[4];

            dtSing.Rows.Add(new object[] { lRecord++, dt, sReader, sGuest, sBasis });

        }

        private void handleTapRecords(string[] aParts)
        {
            if (aParts.Length != 5)
                throw new Exception("Invalid TAP data!");

            long lTIme;
            if (!long.TryParse(aParts[0], out lTIme))
                throw new Exception("Invalid TAP data timestamp!");

            // calculate UTC time
            DateTime dtEpoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
            DateTime dt = dtEpoch + TimeSpan.FromMilliseconds(lTIme);
            if (dt < dtEarliest)
                dtEarliest = dt;
            if (dt > dtLatest)
                dtLatest = dt;
            string sReader = aParts[2];
            string sGuest = aParts[3];
            string sRFID = aParts[4];

            dtTap.Rows.Add(new object[] { lRecord++, dt, sReader, sGuest, sRFID });
        }

        private void handleLrrRecord(string[] aParts)
        {
            if (aParts.Length != 9)
                throw new Exception("Invalid LRR data!");

            long lTIme;
            if (!long.TryParse(aParts[0], out lTIme))
                throw new Exception("Invalid LRR data timestamp!");

            // calculate UTC time
            DateTime dtEpoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
            DateTime dt = dtEpoch + TimeSpan.FromMilliseconds(lTIme);
            if (dt < dtEarliest)
                dtEarliest = dt;
            if (dt > dtLatest)
                dtLatest = dt;
            string sReader = aParts[2];
            string sGuest = aParts[3];
            string sLRID = aParts[4];
            int nPno, nSS, nFreq, nChan;
            if (!int.TryParse(aParts[5], out nPno) ||
                !int.TryParse(aParts[6], out nSS) ||
                !int.TryParse(aParts[7], out nFreq) ||
                !int.TryParse(aParts[8], out nChan))
            {
                throw new Exception("Invalid LRR data!");
            }

            dtLrr.Rows.Add(new object[] { lRecord++, dt, sReader, sGuest, sLRID, nPno, nSS, nFreq, nChan });
        }

        public System.Xml.Schema.XmlSchema GetSchema()
        {
            return null;
        }

        public void ReadXml(XmlReader rdr)
        {
            if (rdr.Name != "xbrcdataset")
                throw new Exception("Invalid dataset file");

            for (; ; )
            {
                // read the next element
                rdr.Read();

                // at end?
                if (rdr.Name == "xbrcdataset" && rdr.NodeType == XmlNodeType.EndElement)
                    break;

                switch (rdr.Name)
                {
                    case "description":
                        {
                            // grab items from attributes
                            sName = rdr.GetAttribute("name");
                            dsType = rdr.GetAttribute("type") == "Raw" ? DataSetType.Raw : DataSetType.Spatial;
                            dtCreated = parseDate(rdr.GetAttribute("date"));

                            if (dsType == DataSetType.Spatial)
                                dblFeigSpacing = double.Parse(rdr.GetAttribute("feigspacing"));

                            if (rdr.IsEmptyElement)
                                sDescription = "";
                            else
                            {
                                // get the text
                                rdr.Read();
                                if (rdr.NodeType != XmlNodeType.Text)
                                    throw new Exception("expected description text");
                                sDescription = rdr.Value;

                                // read  the close tag
                                rdr.Read();
                                if (rdr.Name != "description" && rdr.NodeType != XmlNodeType.EndElement)
                                    throw new Exception("expected </description>");
                            }

                            break;
                        }

                    case "lrrtable":
                        {
                            // get the count
                            int cRow = int.Parse(rdr.GetAttribute("count"));

                            for (int i=0; i<cRow; i++)
                            {
                                // advance
                                rdr.Read();

                                // verify
                                if (rdr.Name != "lrrrow" || rdr.NodeType != XmlNodeType.Element)
                                    throw new Exception("expected <lrrrow> element");

                                // get attributes
                                DataRow row = dtLrr.NewRow();
                                row["Record"] = int.Parse(rdr.GetAttribute("record"));
                                DateTime dt = parseDate(rdr.GetAttribute("time"));
                                if (dt < dtEarliest)
                                    dtEarliest = dt;
                                if (dt > dtLatest)
                                    dtLatest = dt;
                                row["Timestamp"] = dt;
                                row["Reader"] = rdr.GetAttribute("reader");
                                row["Guest"] = rdr.GetAttribute("guest");
                                row["LRID"] = rdr.GetAttribute("lrid");
                                row["Pno"] = int.Parse(rdr.GetAttribute("pno"));
                                row["Freq"] = int.Parse(rdr.GetAttribute("freq"));
                                row["Channel"] = int.Parse(rdr.GetAttribute("chan"));

                                // advance to the value node
                                rdr.Read();
                                if (rdr.NodeType != XmlNodeType.Text)
                                    throw new Exception("unknown element type in dataset file");

                                row["SS"] = int.Parse(rdr.Value);
                                dtLrr.Rows.Add(row);

                                // advance past the close tag
                                rdr.Read();
                                if (rdr.Name!="lrrrow" && rdr.NodeType != XmlNodeType.EndElement)
                                    throw new Exception("expected </lrrrow>");

                            }

                            // end node
                            if (cRow > 0)
                            {
                                rdr.Read();
                                if (rdr.Name != "lrrtable" && rdr.NodeType != XmlNodeType.EndElement)
                                    throw new Exception("expected </lrrtable>");
                            }
                            break;
                        }

                    case "taptable":
                        {
                            // get the count
                            int cRow = int.Parse(rdr.GetAttribute("count"));

                            for (int i = 0; i < cRow; i++)
                            {
                                // advance
                                rdr.Read();

                                // verify
                                if (rdr.Name!="taprow" || rdr.NodeType != XmlNodeType.Element)
                                    throw new Exception("expected <taprow> element");

                                // get attributes
                                DataRow row = dtTap.NewRow();
                                row["Record"] = int.Parse(rdr.GetAttribute("record"));
                                DateTime dt = parseDate(rdr.GetAttribute("time"));
                                if (dt < dtEarliest)
                                    dtEarliest = dt;
                                if (dt > dtLatest)
                                    dtLatest = dt;
                                row["Timestamp"] = dt;
                                row["Reader"] = rdr.GetAttribute("reader");
                                row["Guest"] = rdr.GetAttribute("guest");
                                row["RFID"] = rdr.GetAttribute("rfid");
                                dtTap.Rows.Add(row);

                                if (!rdr.IsEmptyElement)
                                    throw new Exception("expected empty </taprow> element");

                            }

                            // end node
                            if (cRow > 0)
                            {
                                rdr.Read();
                                if (rdr.Name != "taptable" && rdr.NodeType != XmlNodeType.EndElement)
                                    throw new Exception("expected </taptable>");
                            }
                            break;
                        }

                    case "singtable":
                        {
                            // get the count
                            int cRow = int.Parse(rdr.GetAttribute("count"));

                            for (int i = 0; i < cRow; i++)
                            {
                                // advance
                                rdr.Read();

                                // verify
                                if (rdr.Name != "singrow" || rdr.NodeType != XmlNodeType.Element)
                                    throw new Exception("expected <singrow> element");

                                // get attributes
                                DataRow row = dtSing.NewRow();
                                row["Record"] = int.Parse(rdr.GetAttribute("record"));
                                DateTime dt = parseDate(rdr.GetAttribute("time"));
                                row["Timestamp"] = dt;
                                row["Location"] = rdr.GetAttribute("location");
                                row["Guest"] = rdr.GetAttribute("guest");
                                row["Basis"] = rdr.GetAttribute("basis");
                                dtSing.Rows.Add(row);

                                if (!rdr.IsEmptyElement)
                                    throw new Exception("expected empty </singrow> element");

                            }

                            // end node
                            if (cRow > 0)
                            {
                                rdr.Read();
                                if (rdr.Name != "singtable" && rdr.NodeType != XmlNodeType.EndElement)
                                    throw new Exception("expected </singtable>");
                            }
                            break;
                        }

                    default:
                        throw new Exception("unknown element type in dataset file");
                }
            }
        }

        public static DateTime parseDate(string s)
        {
            DateTime dt = DateTime.MinValue;
            try
            {
                dt = DateTime.ParseExact(s, "dd/MM/yyyy HH:mm:ss.fff", CultureInfo.InvariantCulture, DateTimeStyles.AdjustToUniversal );
            }
            catch (Exception)
            {
            }
            return dt;
        }

        public static string formatDate(DateTime dt)
        {
            return dt.ToLocalTime().ToString("dd/MM/yyyy HH:mm:ss.fff");
        }

        public static string formatDateUTC(DateTime dt)
        {
            return dt.ToString("dd/MM/yyyy HH:mm:ss.fff");
        }

        public static string formatDate2(DateTime dt)
        {
            return dt.ToLocalTime().ToString("dd/MM/yyyy\nHH:mm:ss.fff");
        }

        public static int mapFrequencyToSlot(int nFrequency)
        {
            int iFrequency = 0;
            switch (nFrequency)
            {
                case 2401:
                    iFrequency = 0;
                    break;

                case 2424:
                    iFrequency = 1;
                    break;

                case 2450:
                    iFrequency = 2;
                    break;

                case 2476:
                    iFrequency = 3;
                    break;

                default:
                    throw new Exception("Invalid frequency value in dataset");
            }

            return iFrequency;
        }

        public void WriteXml(XmlWriter writer)
        {
            writer.WriteStartElement("description");
            writer.WriteAttributeString("name", sName);
            writer.WriteAttributeString("type", dsType.ToString());
            writer.WriteAttributeString("date", formatDateUTC(dtCreated));
            if (dsType == DataSetType.Spatial)
                writer.WriteAttributeString("feigspacing", dblFeigSpacing.ToString());
            writer.WriteValue(sDescription);
            writer.WriteEndElement();
            writer.WriteStartElement("lrrtable");
            writer.WriteAttributeString("count", dtLrr.Rows.Count.ToString());
            foreach (DataRow row in dtLrr.Rows)
            {
                writer.WriteStartElement("lrrrow");
                writer.WriteAttributeString("record", row["Record"].ToString());
                DateTime dt = (DateTime)row["Timestamp"];
                writer.WriteAttributeString("time", formatDateUTC(dt));
                writer.WriteAttributeString("reader", row["Reader"].ToString());
                writer.WriteAttributeString("guest", row["Guest"].ToString());
                writer.WriteAttributeString("lrid", row["LRID"].ToString());
                writer.WriteAttributeString("pno", row["Pno"].ToString());
                writer.WriteAttributeString("freq", row["Freq"].ToString());
                writer.WriteAttributeString("chan", row["Channel"].ToString());
                writer.WriteValue(row["SS"].ToString());
                writer.WriteEndElement();
            }
            writer.WriteEndElement();
            writer.WriteStartElement("taptable");
            writer.WriteAttributeString("count", dtTap.Rows.Count.ToString());
            foreach (DataRow row in dtTap.Rows)
            {
                writer.WriteStartElement("taprow");
                writer.WriteAttributeString("record", row["Record"].ToString());
                DateTime dt = (DateTime)row["Timestamp"];
                writer.WriteAttributeString("time", formatDateUTC(dt));
                writer.WriteAttributeString("reader", row["Reader"].ToString());
                writer.WriteAttributeString("guest", row["Guest"].ToString());
                writer.WriteAttributeString("rfid", row["RFID"].ToString());
                writer.WriteEndElement();
            }
            writer.WriteEndElement();
            writer.WriteStartElement("singtable");
            writer.WriteAttributeString("count", dtSing.Rows.Count.ToString());
            foreach (DataRow row in dtSing.Rows)
            {
                writer.WriteStartElement("singrow");
                writer.WriteAttributeString("record", row["Record"].ToString());
                DateTime dt = (DateTime)row["Timestamp"];
                writer.WriteAttributeString("time", formatDateUTC(dt));
                writer.WriteAttributeString("location", row["Location"].ToString());
                writer.WriteAttributeString("guest", row["Guest"].ToString());
                writer.WriteAttributeString("basis", row["Basis"].ToString());
                writer.WriteEndElement();
            }
            writer.WriteEndElement();
        }
    }
}
