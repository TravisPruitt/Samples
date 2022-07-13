using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.SqlServer.Management.Smo;
using Microsoft.SqlServer.Management.Common;
using Microsoft.SqlServer.Management.Sdk.Sfc;
using System.IO;

namespace ScriptGenerator
{
    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length != 5)
            {
                Console.WriteLine("Usage:");
                Console.WriteLine("ScriptGenerator,exe <servername> <databasename> <username> <password> <outputdirectory>");
                Console.WriteLine();
                Console.WriteLine("ScriptGenerator.exe localhost IDMS sa <mysapassword> c:\temp");
                return;
            }

            Server server = new Server(args[0]);
            server.ConnectionContext.LoginSecure = false;
            server.ConnectionContext.Login = args[2];
            server.ConnectionContext.Password = args[3];

            Database database = server.Databases[args[1]];

            string tableFileName = Path.Combine(args[4], String.Concat(args[1], "-createtables.sql")).ToLower();

            Scripter scripter = new Scripter(server);

            scripter.Options.DriAll = true;
            scripter.Options.AnsiFile = true;
            scripter.Options.ClusteredIndexes = true;
            scripter.Options.Default = true;
            scripter.Options.Indexes = true;
            scripter.Options.IncludeHeaders = true;
            scripter.Options.AppendToFile = true;
            scripter.Options.FileName = tableFileName;
            scripter.Options.ToFileOnly = true;
//            scripter.Options.IncludeIfNotExists = true;

            if(File.Exists(tableFileName))
            {
                File.Delete(tableFileName);
            }

            if (!Directory.Exists(args[2]))
            {
                Directory.CreateDirectory(args[2]);
            }

            using (StreamWriter writer = File.CreateText(tableFileName))
            {
                writer.WriteLine("use [$(databasename)]");
                writer.WriteLine(String.Empty);
            }

            foreach (Schema schema in database.Schemas)
            {
                if (!schema.IsSystemObject)
                {
                    scripter.Script(new Urn[] { schema.Urn });
                }
            }

            scripter.Options.WithDependencies = true;

            foreach (Table table in database.Tables)
            {
                if (!table.IsSystemObject)
                {
                    scripter.Script(new Urn[] { table.Urn });
                }
            }

            string procedureFileName = Path.Combine(args[4], String.Concat(args[1], "-createprocedures.sql")).ToLower();

            if (File.Exists(procedureFileName))
            {
                File.Delete(procedureFileName);
            }

            using (StreamWriter writer = File.CreateText(procedureFileName))
            {
                writer.WriteLine("use [$(databasename)]");
                writer.WriteLine(String.Empty);
            }

            Scripter procedureScripter = new Scripter(server);

            procedureScripter.Options.DriAll = true;
            procedureScripter.Options.AnsiFile = true;
            procedureScripter.Options.ClusteredIndexes = true;
            procedureScripter.Options.Default = true;
            procedureScripter.Options.IncludeHeaders = true;
            procedureScripter.Options.AppendToFile = true;
            procedureScripter.Options.FileName = procedureFileName;
            procedureScripter.Options.ToFileOnly = true;
//            procedureScripter.Options.IncludeIfNotExists = true;
            //procedureScripter.Options.WithDependencies = true;

            foreach (StoredProcedure storedProcedure in database.StoredProcedures)
            {
                if (!storedProcedure.IsSystemObject)
                {
                   procedureScripter.Script(new Urn[] { storedProcedure.Urn });
                }
            }
        }
    }
}
