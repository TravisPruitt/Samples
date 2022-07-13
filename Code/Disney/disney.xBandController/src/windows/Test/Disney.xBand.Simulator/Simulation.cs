using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Disney.xBand.Simulator
{

    public class Simulation
    {
        private CancellationTokenSource tokenSource;
        private Dto.Attraction attraction;
        private Attraction taskAttraction;

        public bool InProgress { get; private set; }

        public Simulation()
        {
            this.tokenSource = new CancellationTokenSource();
            this.InProgress = false;
        }

        public void Start(Dto.Attraction attraction)
        {
            this.attraction = attraction;

            Task task = new Task((obj) =>
            {
                Dto.Repositories.IGuestRepository guestRepository = new Dto.Repositories.GuestRepository();

                this.taskAttraction = new Attraction(this.attraction, guestRepository, tokenSource);

                this.taskAttraction.Start();

            }, null, tokenSource.Token);

            task.Start();

            this.InProgress = true;

        }

        public void Stop()
        {
            this.tokenSource.Cancel();

            this.InProgress = false;
        }
    }
}
