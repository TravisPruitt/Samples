using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Models
{
    public static class ExtensionMethods
    {
        /// <summary>
        ///     Converts value of an Enum to a string, when the Enum alue has a <see cref="DescriptionAttribute"/>.
        /// </summary>
        /// <param name="value">Enum value</param>
        /// <returns>Description of what the Enum value represents.</returns>
        public static string GetDescription(this Enum value)
        {
            //Determine the type.
            Type type = value.GetType();

            //Get the nme of the value.
            string name = Enum.GetName(type, value);
            if (name != null)
            {
                //Find the field.
                FieldInfo field = type.GetField(name);
                if (field != null)
                {
                    //Get the Description attribute, if any.
                    DescriptionAttribute attr = 
                        Attribute.GetCustomAttribute(field, typeof(DescriptionAttribute)) as DescriptionAttribute;
                    if (attr != null)
                    {
                        //Return the description.
                        return attr.Description;
                    }
                }
            }

            //Return null as the Enum is not annotated with a Description attribute.
            return null;
        }

    }
}
