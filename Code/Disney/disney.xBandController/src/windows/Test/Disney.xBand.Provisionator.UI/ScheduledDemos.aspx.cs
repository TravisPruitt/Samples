using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using Disney.xBand.Provisionator.Repositories;

namespace Disney.xBand.Provisionator.UI
{
    public partial class ScheduledDemos : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                IScheduledDemoRepository walkthroughRepository = new ScheduledDemoRepository();

                this.scheduledDemoDropDownList.DataSource = walkthroughRepository.GetScheduledDemos();
                this.scheduledDemoDropDownList.DataBind();

            }
        }

        protected void scheduledDemoDropDownList_SelectedIndexChanged(object sender, EventArgs e)
        {
            int walkthroughID = Int32.Parse(scheduledDemoDropDownList.SelectedValue);

            IScheduledDemoRepository walkthroughRepository = new ScheduledDemoRepository();
            this.scheduledDemoGuestDataGrid.DataSource = walkthroughRepository.GetGuests(walkthroughID);
            this.scheduledDemoGuestDataGrid.DataBind();

        }
        
        protected void scheduledDemoGuestDataGrid_ItemCommand(Object sender, DataGridCommandEventArgs e)
        {
            int guestID = Int32.Parse(e.Item.Cells[3].Text);

            switch (e.CommandName)
            {
                //Remove guest from walkthrough
                case "Remove":
                    {
                        break;
                    }

                //Move guest to another walkthrough
                case "Move":
                    {
                        break;
                    }

                //Edit guest information including band
                case "Edit":
                    {
                        //  show the modal popup
                        //this.guestDetailsPopup.Show();
                        break;
                    }
            }
        }

    }
}