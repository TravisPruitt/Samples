using System;
using System.Collections.Generic;
using System.Text;

namespace GFFSimulator
{
    class Table
    {
        public string Name { get; set; }
        public int Size { get; set; }
        private DateTime dtWhenFree = DateTime.MinValue;

        public void Occupy(DateTime dtWhenFree)
        {
            this.dtWhenFree = dtWhenFree;
        }

        public bool IsOccupied(DateTime dt)
        {
            return dtWhenFree > dt;
        }

        public DateTime WhenFree
        {
            get
            {
                return dtWhenFree;
            }
        }
    }
}
