using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using System.Xml;
using System.IO;
using System.Runtime.Serialization.Json;

namespace Disney.xBand.IDMS
{
    public class Serializer
    {
        public static T Deserialize<T>(string message)
        {
            T result = default(T);

            using (MemoryStream memStream = new MemoryStream(Encoding.UTF8.GetBytes(message)))
            {
                // deserialize the response 
                DataContractJsonSerializer deserializer = new DataContractJsonSerializer(typeof(T));
                result = (T) deserializer.ReadObject(memStream);
            }
 
            return result;
        }

        public static byte[] Serialize<T>(T item)
        {
            DataContractJsonSerializer serializer = new DataContractJsonSerializer(typeof(T));
            MemoryStream ms = new MemoryStream();
            serializer.WriteObject(ms, item);

            string json = Encoding.Default.GetString(ms.ToArray());
            ms.Close();

            return Encoding.UTF8.GetBytes(json);
        }
    }
}
