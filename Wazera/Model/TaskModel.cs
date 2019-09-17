using System.Collections.Generic;
using System.Linq;
using Wazera.Data;
using WazeraSQL;

namespace Wazera.Model
{
    class TaskModel : Entity<TaskModel>
    {
        static TaskModel()
        {
            TableName = "Task";
            Columns = new EntityColumn[]
            {
                new EntityColumn("ID", EntityColumnType.Long, true, true),
                new EntityColumn("StatusID", EntityColumnType.Long, true),
                new EntityColumn("Name", EntityColumnType.String50, true),
                new EntityColumn("Description", EntityColumnType.Text, true),
                new EntityColumn("SortOrder", EntityColumnType.Int, true),
                new EntityColumn("PriorityID", EntityColumnType.Long, true),
                new EntityColumn("UserID", EntityColumnType.Long, true)
            };
            CreateTableIfNotExists();
        }

        public long ID { get; set; }

        public long StatusID { get; set; }

        public string Name { get; set; }

        public string Description { get; set; }

        public int SortOrder { get; set; }

        public long PriorityID { get; set; }

        public long UserID { get; set; }

        public TaskModel()
        {

        }

        public TaskModel(TaskData taskData)
        {
            ID = taskData.ID;
            StatusID = taskData.Status.ID;
            Name = taskData.Name;
            Description = taskData.Description;
            SortOrder = taskData.SortOrder;
            PriorityID = taskData.Priority.ID;
            UserID = taskData.User.ID;
        }

        public TaskData ToData(StatusData statusData)
        {
            return new TaskData(Name, Description, PriorityData.PriorityMap[PriorityID], LoggedIn.User, statusData)
            {
                ID = ID
            };
        }

        public static void FillStatus(StatusData statusData)
        {
            statusData.Tasks.Clear();

            List<TaskModel> tasks = Find(new string[] { "StatusID = " + statusData.ID })
                .OrderBy(task => task.SortOrder)
                .ThenBy(task => task.ID)
                .ToList();

            foreach(TaskModel taskModel in tasks)
            {
                TaskData taskData = taskModel.ToData(statusData);
                statusData.Tasks.Add(taskData);
            }
        }

        public static void DeleteById(long id)
        {
            Delete(new string[] { "ID = " + id });
        }

        public static void DeleteByStatusId(long id)
        {
            Delete(new string[] { "StatusID = " + id });
        }
    }
}
