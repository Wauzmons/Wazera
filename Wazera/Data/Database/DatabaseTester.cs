using System;
using System.Data;
using System.IO;

namespace Wazera.Data.Database
{
    class DatabaseTester
    {
        private static DataSet dataSet = new DataSet();

        private static string savePath = AppDomain.CurrentDomain.BaseDirectory + "\\ saves.xml";

        public static void Start()
        {
            if(File.Exists(savePath))
            {
                dataSet.ReadXml(savePath);
            }
            else
            {
                CreateSchema();
            }
            Console.WriteLine(dataSet.Tables.Count);
        }

        private static void CreateSchema()
        {
            DataTable taskDataTable = dataSet.Tables.Add("Tasks");
            DataColumn column_task_id = taskDataTable.Columns.Add("ID", typeof(Int64));
            column_task_id.AutoIncrement = true;
            taskDataTable.Columns.Add("Name", typeof(Int64));
            taskDataTable.Columns.Add("Description", typeof(Int64));
            taskDataTable.Columns.Add("PriorityID", typeof(Int64));
            taskDataTable.Columns.Add("UserID", typeof(Int64));
            taskDataTable.Columns.Add("StatusID", typeof(Int64));
            taskDataTable.PrimaryKey = new DataColumn[] { column_task_id };

            dataSet.AcceptChanges();
            dataSet.WriteXml(savePath);
        }
    }
}
