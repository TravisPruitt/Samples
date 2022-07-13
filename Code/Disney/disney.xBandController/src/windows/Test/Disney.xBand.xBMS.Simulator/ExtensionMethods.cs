using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Reflection;
using System.ComponentModel;
using System.IO;
using System.Xml.Serialization;
using System.Xml;
using System.Runtime.Serialization.Json;

namespace Disney.xBand.xBMS.Simulator
{
    public static class ExtensionMethods
    {
        public static string GetDescription(this Enum value)
        {
            Type type = value.GetType();
            string name = Enum.GetName(type, value);
            if (name != null)
            {
                FieldInfo field = type.GetField(name);
                if (field != null)
                {
                    DescriptionAttribute attr = Attribute.GetCustomAttribute(field, typeof(DescriptionAttribute)) as DescriptionAttribute;
                    if (attr != null)
                    {
                        return attr.Description;
                    }
                }
            }

            return null;
        }

        public static string ToXml<T>(this T input)
        {
            //Create our own namespaces for the output
            XmlSerializerNamespaces ns = new XmlSerializerNamespaces();

            //Add an empty namespace and empty value
            ns.Add("", ""); 
            
            XmlSerializer serializer = new XmlSerializer(input.GetType());

            using (MemoryStream memoryStream = new MemoryStream())
            {
                using (XmlTextWriter textWriter = new XmlTextWriter(memoryStream, null))
                {
                    //textWriter.Namespaces = false;
                    textWriter.Formatting = Formatting.Indented;
                    serializer.Serialize(textWriter, input, ns);

                    return Encoding.Default.GetString(memoryStream.ToArray());
                }
            }
        }

        public static string ToJson<T>(this T input)
        {
            DataContractJsonSerializer s = new DataContractJsonSerializer(input.GetType());
            MemoryStream ms = new MemoryStream();
            s.WriteObject(ms, input);

            string json = Encoding.Default.GetString(ms.ToArray());
            ms.Close();

            return json;
        }

    }
}
