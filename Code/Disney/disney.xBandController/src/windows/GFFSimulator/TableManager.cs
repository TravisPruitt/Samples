using System;
using System.Collections.Generic;
using System.Text;

namespace GFFSimulator
{
    class TableManager
    {
        private Dictionary<string, Table> dicTables = new Dictionary<string, Table>();

        public void AddTable(string sName, int nSize)
        {
            Table t = new Table();
            t.Name = sName;
            t.Size = nSize;
            dicTables.Add(sName, t);
        }

        public Table FindAvailableTable(DateTime dt, int nSize)
        {
            foreach (Table t in dicTables.Values)
                if (!t.IsOccupied(dt) && t.Size == nSize)
                    return t;

            return null;
        }

    }
}
