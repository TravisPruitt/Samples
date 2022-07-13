using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;

namespace xTRC
{
    [Guid("82516862-0522-41F9-94F8-E273B80D3479"), InterfaceType(ComInterfaceType.InterfaceIsDual)]
    [ComVisible(true)]
    public interface ITapInterface
    {
        void SendTap(string secureId, string uid);
    }
}
