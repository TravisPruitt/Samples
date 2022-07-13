using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

namespace Disney.xBand.xTRC
{
    [Guid("C9B69263-F158-4D2D-872D-7FD574B94897"), InterfaceType(ComInterfaceType.InterfaceIsDual)]
    [ComVisible(true)]
    public interface ITapInterface
    {
       void SendTap(string secureId, string uid);
    }
}
