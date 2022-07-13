using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.IDMS.Status
{
    public interface IStatusDao
    {
        StatusResult GetStatus();
    }
}
