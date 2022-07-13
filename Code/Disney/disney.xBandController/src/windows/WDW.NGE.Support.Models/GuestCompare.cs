using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models
{
    public class GuestCompare : ModelBase<GuestCompare>
    {
        private Services.IIDMSServiceAgent idmsServiceAgent;
        private Services.IOneViewServiceAgent oneViewServiceAgent;
        private Services.IXBMSServiceAgent xbmsServiceAgent;

        private GuestCompareStatus status;
        public GuestCompareStatus Status
        {
            get { return status; }
            set
            {
                status = value;
                NotifyPropertyChanged(m => m.Status);
            }
        }

        private bool useInternalGuestIdentifier;
        private Common.GuestIdentifier internalGuestIdentifier;

        private Common.GuestIdentifier guestIdentifier;
        public Common.GuestIdentifier GuestIdentifier
        {
            get { return guestIdentifier; }
            set
            {
                guestIdentifier = value;
                NotifyPropertyChanged(m => m.GuestIdentifier);
            }
        }

        private IDMS.GuestProfile idmsGuestProfile;
        public IDMS.GuestProfile IdmsGuestProfile
        {
            get { return idmsGuestProfile; }
            set
            {
                idmsGuestProfile = value;
                NotifyPropertyChanged(m => m.IdmsGuestProfile);
            }
        }

        private OneView.GuestProfile oneViewGuestProfile;
        public OneView.GuestProfile OneViewGuestProfile
        {
            get { return oneViewGuestProfile; }
            protected set
            {
                oneViewGuestProfile = value;
                NotifyPropertyChanged(m => m.OneViewGuestProfile);
            }
        }

        private List<String> guestLocators;
        public List<String> GuestLocators
        {
            get
            {
                if (this.guestLocators != null)
                {
                    if (!this.guestLocators.Contains("visualId"))
                    {
                        this.guestLocators.Add("visualId");
                    }
                    this.guestLocators.Sort();
                    return this.guestLocators;
                }

                return null;
            }
            set
            {
                guestLocators = value;
                NotifyPropertyChanged(m => m.GuestLocators);
            }
        }

        public List<Common.GuestIdentifier> TransferIds
        {
            get
            {
                if (this.oneViewGuestProfile != null)
                {
                    if (this.oneViewGuestProfile.GuestIdentifiers != null)
                    {
                        return this.OneViewGuestProfile.GuestIdentifiers.Where(g => g.Match == false && g.IdentifierType != "xbandid").ToList();
                    }
                }

                return null;
            }
        }


        public void UpdateName()
        {
            if (this.OneViewGuestProfile != null)
            {
                this.idmsServiceAgent.UpdateName(this.idmsGuestProfile.GuestId,
                    OneViewGuestProfile.FirstName, oneViewGuestProfile.LastName);

                this.IdmsGuestProfile = GetIdmsGuestProfile(null);
                Compare();
            }
        }

        public GuestCompare()
        {
            this.guestIdentifier = new Models.Common.GuestIdentifier();
            this.idmsServiceAgent = new Services.IDMSServiceAgent();
            this.oneViewServiceAgent = new Services.OneViewServiceAgent();
            this.xbmsServiceAgent = new Services.XBMSServiceAgent();

            Models.Services.ServiceResult<Models.IDMS.GuestLocators> guestLocators = idmsServiceAgent.GetGuestLocators();

            this.guestLocators = new List<string>();

            if (guestLocators.Status == Models.Services.ServiceCallStatus.OK)
            {
                foreach (string guestLocatorItem in guestLocators.Result.GuestLocatorList)
                {
                    this.GuestLocators.Add(guestLocatorItem);
                }
            }
            else
            {
                this.GuestLocators.Add("gxp-link-id");
                this.GuestLocators.Add("dme-link-id");
                this.GuestLocators.Add("xbms-link-id");
                this.GuestLocators.Add("xid");
                this.GuestLocators.Add("FBID");
                this.GuestLocators.Add("swid");
                this.GuestLocators.Add("transactional-guest-id");
                this.GuestLocators.Add("gff-bog-link-id");
                this.GuestLocators.Add("admission-link-id");
                this.GuestLocators.Add("payment-link-id");
                this.GuestLocators.Add("media-link-id");
                this.GuestLocators.Add("leveln-link-id");
                this.GuestLocators.Add("bog-xedc-link-id");
                this.GuestLocators.Add("bog-link-id");
            }
            NotifyPropertyChanged(m => m.GuestLocators);

            this.useInternalGuestIdentifier = false;
        }

        public void RemoveExtraIdentifiers()
        {
            if (oneViewGuestProfile != null && idmsGuestProfile != null)
            {
                foreach (Models.Common.GuestIdentifier extraGuestIdentifier in idmsGuestProfile.GuestIdentifiers.Where(g => g.Match == false).ToList())
                {
                    this.idmsServiceAgent.RemoveGuestIdentifier(this.idmsGuestProfile.GuestId, extraGuestIdentifier);
                }

                this.IdmsGuestProfile = GetIdmsGuestProfile(null);

                Compare();
            }
        }

        public void AddMissingBands()
        {
            if (this.oneViewGuestProfile != null && this.idmsGuestProfile != null)
            {
                foreach (Models.Common.GuestIdentifier guestIdentifier in
                    oneViewGuestProfile.GuestIdentifiers.Where(g => g.Match == false && g.IdentifierType == "xbandid").ToList())
                {
                    this.idmsServiceAgent.AssignBand(idmsGuestProfile.GuestId, guestIdentifier.IdentifierValue);
                }

                this.IdmsGuestProfile = GetIdmsGuestProfile(null);

                this.Compare();
            }
        }

        public void AddMissingIdentifiers()
        {
            if (this.oneViewGuestProfile != null && this.idmsGuestProfile != null)
            {
                foreach (Models.Common.GuestIdentifier guestIdentifier in this.TransferIds)
                {
                    this.idmsServiceAgent.MoveIdentifier(idmsGuestProfile.GuestId, guestIdentifier);
                }

                this.IdmsGuestProfile = GetIdmsGuestProfile(null);

                Compare();
            }
        }

        public void Compare()
        {
            this.Status = GuestCompareStatus.Match;
            this.IdmsGuestProfile = null;
            this.OneViewGuestProfile = null;

            if (String.Compare(this.guestIdentifier.IdentifierType, "visualId", true) == 0)
            {
                Models.Services.ServiceResult<Models.xBMS.XbandDetails> serviceResult =
                    this.xbmsServiceAgent.GetXbandDetailsByVisualId(this.guestIdentifier.IdentifierValue);

                if (serviceResult.Status == Services.ServiceCallStatus.OK)
                {
                    this.internalGuestIdentifier = new Common.GuestIdentifier()
                    {
                        IdentifierType = serviceResult.Result.GuestIdType,
                        IdentifierValue = serviceResult.Result.GuestId
                    };
                    useInternalGuestIdentifier = true;

                    this.OneViewGuestProfile = GetOneViewGuestProfile(this.internalGuestIdentifier);
                    if (this.OneViewGuestProfile != null)
                    {
                        this.IdmsGuestProfile = GetIdmsGuestProfile(this.OneViewGuestProfile.XID);
                    }

                }
                else
                {
                    this.Status |= GuestCompareStatus.BandNotFound;
                }
            }
            else
            {
                useInternalGuestIdentifier = false;
                this.OneViewGuestProfile = GetOneViewGuestProfile(guestIdentifier);
                if (this.OneViewGuestProfile != null)
                {
                    this.IdmsGuestProfile = GetIdmsGuestProfile(this.OneViewGuestProfile.XID);
                }
                else
                {
                    this.IdmsGuestProfile = GetIdmsGuestProfile(null);
                }
            }


            if (this.idmsGuestProfile != null && this.oneViewGuestProfile != null)
            {
                MatchIdentifiers(idmsGuestProfile, oneViewGuestProfile.GuestIdentifiers);
                MatchIdentifiers(oneViewGuestProfile, idmsGuestProfile.GuestIdentifiers);
                CheckName();
                CheckExtraIdentifiers();
                CheckMissingIdentifiers();
                CheckMissingBands();
            }
            else if (this.idmsGuestProfile == null)
            {
                if ((this.Status & GuestCompareStatus.IDMSTimeout) != GuestCompareStatus.IDMSTimeout)
                {
                    this.Status |= GuestCompareStatus.GuestMissingFromIDMS;
                }
            }
            else
            {
                if ((this.Status & GuestCompareStatus.OneViewTimeout) != GuestCompareStatus.OneViewTimeout)
                {
                    this.Status = GuestCompareStatus.GuestMissingFromOneView;
                }
            }

            NotifyPropertyChanged(m => m.TransferIds);
        }

        private Models.IDMS.GuestProfile GetIdmsGuestProfile(string xid)
        {

            Models.Services.ServiceResult<Models.Common.IGuestProfile> serviceResult = null;

            if (!useInternalGuestIdentifier)
            {
                serviceResult = idmsServiceAgent.GetGuestProfile(this.guestIdentifier);
            }
            else
            {
                serviceResult = idmsServiceAgent.GetGuestProfile(this.internalGuestIdentifier);
            }

            //If not found by specified ID, try xid from OneView
            if (serviceResult.Status == Services.ServiceCallStatus.NotFound)
            {
                if (!String.IsNullOrEmpty(xid))
                {
                    Models.Common.GuestIdentifier gi = new Common.GuestIdentifier()
                    {
                        IdentifierType = "xid",
                        IdentifierValue = xid
                    };
                    serviceResult = idmsServiceAgent.GetGuestProfile(gi);
                }
            }

            switch (serviceResult.Status)
            {
                case Services.ServiceCallStatus.Unknown:
                    {
                        this.Status |= GuestCompareStatus.GuestMissingFromIDMS;
                        break;
                    }

                case Services.ServiceCallStatus.Timeout:
                    {
                        this.Status |= GuestCompareStatus.IDMSTimeout;
                        break;
                    }

                case Services.ServiceCallStatus.OK:
                    {
                        return (Models.IDMS.GuestProfile)serviceResult.Result;
                    }
            }

            return null;
        }

        private Models.OneView.GuestProfile GetOneViewGuestProfile(Models.Common.GuestIdentifier guestIdentifier)
        {
            Models.Services.ServiceResult<Models.Common.IGuestProfile> serviceResult = oneViewServiceAgent.GetGuestProfile(guestIdentifier);

            switch (serviceResult.Status)
            {
                case Services.ServiceCallStatus.NotFound:
                case Services.ServiceCallStatus.Unknown:
                    {
                        this.Status |= GuestCompareStatus.GuestMissingFromOneView;
                        break;
                    }

                case Services.ServiceCallStatus.Timeout:
                    {
                        this.Status |= GuestCompareStatus.OneViewTimeout;
                        break;
                    }

                case Services.ServiceCallStatus.OK:
                    {
                        return (Models.OneView.GuestProfile)serviceResult.Result;
                    }
            }

            return null;
        }

        public void Fix()
        {
            if (this.Status != GuestCompareStatus.Match &&
                this.Status != GuestCompareStatus.GuestMissingFromIDMS &&
                this.Status != GuestCompareStatus.GuestMissingFromOneView &&
                this.Status != (GuestCompareStatus.GuestMissingFromIDMS | GuestCompareStatus.GuestMissingFromIDMS))
            {
                if ((this.Status & GuestCompareStatus.NameMismatch) == GuestCompareStatus.NameMismatch)
                {
                    this.UpdateName();
                }

                if ((this.Status & GuestCompareStatus.ExtraIDMSIdentifiers) == GuestCompareStatus.ExtraIDMSIdentifiers)
                {
                    this.RemoveExtraIdentifiers();
                }

                if ((this.Status & GuestCompareStatus.MissingIdentifiers) == GuestCompareStatus.MissingIdentifiers)
                {
                    this.AddMissingIdentifiers();
                }

                if ((this.Status & GuestCompareStatus.MissingBands) == GuestCompareStatus.MissingBands)
                {
                    this.AddMissingBands();
                }
            }
        }

        private void MatchIdentifiers(Models.Common.IGuestProfile guestProfile, List<Models.Common.GuestIdentifier> guestIdentifiers)
        {
            foreach (Models.Common.GuestIdentifier guestIdentifier in guestProfile.GuestIdentifiers)
            {
                guestIdentifier.Match = false;

                foreach (Models.Common.GuestIdentifier comparisonGuestIdentifier in guestIdentifiers)
                {
                    if (String.Compare(comparisonGuestIdentifier.IdentifierType, guestIdentifier.IdentifierType, true) == 0 &&
                        String.Compare(comparisonGuestIdentifier.IdentifierValue, guestIdentifier.IdentifierValue, true) == 0)
                    {
                        guestIdentifier.Match = true;
                    }
                }
            }
        }

        private void CheckName()
        {
            if (oneViewGuestProfile != null)
            {
                if (!String.IsNullOrEmpty(oneViewGuestProfile.FirstName) &&
                    !String.IsNullOrEmpty(oneViewGuestProfile.LastName))
                {
                    string oneViewName = String.Format("{0} {1}", oneViewGuestProfile.FirstName, oneViewGuestProfile.LastName);
                    string nameIDMS = String.Format("{0} {1}", idmsGuestProfile.FirstName, idmsGuestProfile.LastName);

                    if (String.Compare(nameIDMS, oneViewName) != 0)
                    {
                        this.Status |= GuestCompareStatus.NameMismatch;
                    }
                }
            }
        }

        private void CheckExtraIdentifiers()
        {
            if (idmsGuestProfile != null)
            {
                if (idmsGuestProfile.GuestIdentifiers.Where(g => g.Match == false).Count() > 0)
                {
                    this.Status |= GuestCompareStatus.ExtraIDMSIdentifiers;
                }
            }
        }

        private void CheckMissingIdentifiers()
        {
            if (this.oneViewGuestProfile != null)
            {
                if (this.oneViewGuestProfile.GuestIdentifiers.Where(g => g.Match == false
                    && !g.IdentifierType.StartsWith("xband")
                    && !g.IdentifierType.StartsWith("guid")).Count() > 0)
                {
                    this.Status |= GuestCompareStatus.MissingIdentifiers;
                }
            }
        }

        private void CheckMissingBands()
        {
            if (this.oneViewGuestProfile != null)
            {
                if (this.oneViewGuestProfile.GuestIdentifiers.Where(g => g.Match == false
                    && g.IdentifierType.StartsWith("xbandid")).Count() > 0)
                {
                    this.Status |= GuestCompareStatus.MissingBands;
                }
            }
        }
    }
}
