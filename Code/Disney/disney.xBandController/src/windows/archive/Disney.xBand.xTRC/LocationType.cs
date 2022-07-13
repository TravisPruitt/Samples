using System;
using System.Collections.Generic;
using System.Text;

namespace Disney.xBand.xTRC
{
    /*
     * Note that this enum is synchronized with Java code. Do not mess with it!
     */
    internal enum LocationType : int
    {
        Unspecified = 0,
        Entrance = 1,
        Waypoint = 2,
        Exit = 3,
        Load = 4,
        InCar = 5,
        Merge = 6,
        xPassEntry = 7,
        Combo = 8
    }
}
