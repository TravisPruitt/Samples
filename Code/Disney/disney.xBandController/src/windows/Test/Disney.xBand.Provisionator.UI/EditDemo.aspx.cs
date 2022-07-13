using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using Disney.xBand.Provisionator.Repositories;

namespace Disney.xBand.Provisionator.UI
{
    public partial class Reset : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                //List<TimeSpan> times = new List<TimeSpan>();

                //for (int hour = 0; hour < 24; hour++)
                //{
                //    for (int minute = 0; minute < 60;)
                //    {
                //        times.Add(new TimeSpan(hour, minute, 0));
                //        minute += 15;
                //    }
                //}

                //this.timeDropDownList.DataSource = times;
                //this.timeDropDownList.DataBind();

                IScheduledDemoRepository repositiory = new ScheduledDemoRepository();
                List<Dto.Demo> scheduledDemos = repositiory.GetScheduledDemos();

                if (Page.Request.Params["scheduled_demo_id"] != null)
                {
                    int scheduledDemoID = Int32.Parse(Page.Request.Params["scheduled_demo_id"]);

                    Dto.Demo scheduledDemo = repositiory.GetScheduledDemo(scheduledDemoID);

                    this.descriptionTextBox.Text = scheduledDemo.DemoDescription;
                }
                else
                {
                    this.saveButton.Text = "Add";
                }

                //int totalHours = Convert.ToInt32(DateTime.Now.TimeOfDay.TotalHours);
                //this.timeDropDownList.SelectedIndex = totalHours * 4;
            }
        }
    }
}