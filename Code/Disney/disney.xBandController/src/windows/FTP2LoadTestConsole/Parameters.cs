using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using System.IO;

namespace TestConsole
{
    [DataContract]
    public class Parameters
    {
        [IgnoreDataMember]
        private const string captureFileDefault = "chart.png";

        [IgnoreDataMember]
        private const string sessionLogFileDefault = "session-{0}.txt";

        // Note "totalTime" is automatically added
        [IgnoreDataMember]
        private string[] SplitNamesDefault = new string[] { "login", 
                                                            "validGuest", 
                                                            "facebookAssociation",
                                                            "selectPark",
                                                            "setPark",
                                                            "selectDay", 
                                                            "setScheduledDate", 
                                                            "selectParty", 
                                                            "selectPartyMembers", 
                                                            "selectExperiences", 
                                                            "setAttractions",
                                                            "selectMagicPasses",
                                                            "selectOfferset", 
                                                            "appointmentConfirmation", 
                                                            "cancelPasses", 
                                                            "cancelPasses2", 
                                                            "cancelPasses3",
                                                            "cancelPassesConfirmation", 
                                                            "cancelMagicPasses"};

        [IgnoreDataMember]
        private const int samplingIntervalDefault = 60;

        [DataMember(Order=0)]
        public string ConfigurationName { get; set; }

        [DataMember(Order = 1)]
        public string CaptureFile { get; set; }

        [DataMember(Order = 2)]
        public string SessionLogFile { get; set; }

        [DataMember(Order = 3)]
        public int SamplingInterval { get; set; }

        [DataMember(Order = 4)]
        public string[] SplitNames { get; set; }

        [IgnoreDataMember]
        private bool[] IsSplitCharted { get; set; }

        [IgnoreDataMember]
        public int CountSplits
        {
            get
            {
                return SplitNames.Length;
            }
        }

        // always the last value
        [IgnoreDataMember]
        public int TotalTime
        {
            get
            {
                return CountSplits - 1;
            }
        }

        // HTML
        [IgnoreDataMember]
        private string sStatusPage = null;

        [IgnoreDataMember]
        public string StatusPage
        {
            get
            {
                if (sStatusPage==null)
                {
                    sStatusPage = Resource.Status;
                    sStatusPage = sStatusPage.Replace("{", "{{");
                    sStatusPage = sStatusPage.Replace("}", "}}");
                    sStatusPage = sStatusPage.Replace("[[", "{");
                    sStatusPage = sStatusPage.Replace("]]", "}");
                }

                return sStatusPage;
            }
        }

        [IgnoreDataMember]
        private static Parameters instance = null;

        public bool GetIsSplitCharted(int i)
        {
            if (i == TotalTime)
                return true;
            else
                return IsSplitCharted[i];
        }

        public void SetIsSplitCharted(int i, bool bCharted)
        {
            if (i != TotalTime)
                IsSplitCharted[i] = bCharted;
        }


        public static Parameters Instance
        {
            get
            {
                if (instance == null)
                    instance = new Parameters();
                return instance;
            }
        }

        private Parameters()
        {
            ConfigurationName = "Timing Test Console";
            CaptureFile = captureFileDefault;
            SessionLogFile = sessionLogFileDefault;
            SetSplitNames(SplitNamesDefault);
            SamplingInterval = samplingIntervalDefault;
            IsSplitCharted = new bool[CountSplits];
            for (int i = 0; i < CountSplits - 1; i++)
                SetIsSplitCharted(i, false);
        }

        public void Reconfigure(string sConfigurationName, string[] asSplitNames)
        {
            if (sConfigurationName!=null)
                ConfigurationName = sConfigurationName;
            if (asSplitNames!=null)
                SetSplitNames(asSplitNames);
        }

        private void SetSplitNames(string[] asSplitNames)
        {
            int cItems = asSplitNames.Length + 1;
            SplitNames = new string[cItems];
            for (int i = 0; i < cItems-1; i++)
                SplitNames[i] = asSplitNames[i];

            // add total time
            SplitNames[cItems-1] = "totalTime";
        }

        public void LoadConfiguration(string sPath)
        {
            DataContractJsonSerializer ser = new DataContractJsonSerializer(typeof(Parameters));
            FileStream fs = new FileStream(sPath, FileMode.Open);
            instance = ser.ReadObject(fs) as Parameters;
            fs.Close();

            // reset these values
            instance.IsSplitCharted = new bool[CountSplits];
            for (int i = 0; i < CountSplits - 1; i++)
                instance.SetIsSplitCharted(i, false);
        }

        public void SaveConfiguration(string sPath)
        {
            DataContractJsonSerializer ser = new DataContractJsonSerializer(typeof(Parameters));
            FileStream fs = new FileStream(sPath, FileMode.Create);
            ser.WriteObject(fs, this);
            fs.Close();
        }


    }
}
