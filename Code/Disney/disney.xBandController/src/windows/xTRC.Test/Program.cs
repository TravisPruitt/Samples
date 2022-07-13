using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Reflection;

namespace xTRC.Test
{
    class Program
    {
        static void Main(string[] args)
        {
            var type = Type.GetTypeFromProgID("xTRC.xBRC");
            var instance = Activator.CreateInstance(type);
            type.InvokeMember("SendTap", BindingFlags.InvokeMethod, null, instance, new object[2] { "123", "123" });
        }
    }
}
