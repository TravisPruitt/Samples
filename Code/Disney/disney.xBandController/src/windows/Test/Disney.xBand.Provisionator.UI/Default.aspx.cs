using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using Disney.xBand.Provisionator.Repositories;

namespace Disney.xBand.Provisionator.UI
{
    public partial class _Default : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {

            if (!IsPostBack)
            {
                IScheduledDemoRepository walkthroughRepository = new ScheduledDemoRepository();

                this.scheduledDemoDataGrid.DataSource = walkthroughRepository.GetScheduledDemos();
                this.scheduledDemoDataGrid.DataBind();

            }
        }

        protected void scheduledDemoDataGrid_ItemCommand(Object sender, DataGridCommandEventArgs e)
        {
            int scheduledDemoID = Int32.Parse(e.Item.Cells[3].Text);

            switch (e.CommandName)
            {
                //Setup walkthrough
                case "Setup":
                    {
                        this.resultsLabel.Text = "Starting...";
                        IScheduledDemoRepository walkthroughRepository = new ScheduledDemoRepository();
                        Dto.AddDemoEntitlementsResult addDemoEntitlementsResult = walkthroughRepository.AddGuests(scheduledDemoID, 10);

                        if (addDemoEntitlementsResult.Successful)
                        {
                            this.resultsLabel.Text = "Demo setup successful";
                        }
                        else
                        {
                            this.resultsLabel.Text = "Demo setup failed ";
                            if (addDemoEntitlementsResult.GenerateOffersetResult.Status != Dto.OffersetStatus.Success)
                            {
                                this.resultsLabel.Text += addDemoEntitlementsResult.GenerateOffersetResult.Message;
                            }
                            else if (addDemoEntitlementsResult.BookOffersetResult.Status != Dto.OffersetStatus.Success)
                            {
                                this.resultsLabel.Text += addDemoEntitlementsResult.BookOffersetResult.Message;
                            }
                        }

                        break;
                    }
                //Remove walkthrough
                case "Remove":
                    {
                        IScheduledDemoRepository walkthroughRepository = new ScheduledDemoRepository();
                        walkthroughRepository.RemoveGuests(scheduledDemoID);
                        break;
                    }

                //Edit walkthrough
                case "Edit":
                    {
                        Response.Redirect(String.Format("~/EditDemo.aspx?scheduled_demo_id={0}", scheduledDemoID));
                        break;
                    }
            }
        }

        //protected void editPanelCloseButton_OnClick(Object sender, EventArgs e)
        //{
        //    this.modalEditor.Hide();
        //    int scheduledDemoID = (scheduledDemoDataGrid.SelectedItem.DataItem as Dto.ScheduledDemo).ScheduledDemoID;
        //    this.ModalProgress.Show();
        //    //IScheduledDemoRepository walkthroughRepository = new ScheduledDemoRepository();
        //    //Dto.AddDemoEntitlementsResult addDemoEntitlementsResult = walkthroughRepository.AddGuests(scheduledDemoID, 10);

        //    //if (!addDemoEntitlementsResult.Successful)
        //    //{

        //    //}
        //    //else
        //    //{

        //    //}
        //    System.Threading.Thread.Sleep(4000);

        //    this.ModalProgress.Hide();
        //}
    }
}
