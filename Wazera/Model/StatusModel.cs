using System.Collections.Generic;
using Wazera.Data;
using WazeraSQL;

namespace Wazera.Model
{
    class StatusModel : Entity<StatusModel>
    {
        static StatusModel()
        {
            TableName = "Status";
            Columns = new EntityColumn[]
            {
                new EntityColumn("ID", EntityColumnType.Long, true, true),
                new EntityColumn("ProjectID", EntityColumnType.Long, true),
                new EntityColumn("Title", EntityColumnType.String50, true),
                new EntityColumn("MinCards", EntityColumnType.Int, true),
                new EntityColumn("MaxCards", EntityColumnType.Int, true),
                new EntityColumn("IsBacklog", EntityColumnType.Bool, true),
                new EntityColumn("IsRelease", EntityColumnType.Bool, true)
            };
            CreateTableIfNotExists();
        }

        public long ID { get; set; }

        public long ProjectID { get; set; }

        public string Title { get; set; }

        public int MinCards { get; set; }

        public int MaxCards { get; set; }

        public bool IsBacklog { get; set; }

        public bool IsRelease { get; set; }

        public StatusModel()
        {
            // Automated Initialization by WazeraSQL
        }

        public StatusModel(StatusData statusData)
        {
            ID = statusData.ID;
            ProjectID = statusData.Project.ID;
            Title = statusData.Title;
            MinCards = statusData.MinCards;
            MaxCards = statusData.MaxCards;
            IsBacklog = statusData.IsBacklog;
            IsRelease = statusData.IsRelease;
        }

        public StatusData ToData(ProjectData projectData)
        {
            return new StatusData(Title, projectData, IsBacklog, IsRelease, MinCards, MaxCards)
            {
                ID = ID
            };
        }

        public static void FillProject(ProjectData projectData)
        {
            projectData.Backlog = null;
            projectData.Statuses.Clear();
            StatusData releaseStatus = null;

            foreach (StatusModel statusModel in Find(new string[] { "ProjectID = " + projectData.ID }))
            {
                StatusData statusData = statusModel.ToData(projectData);

                if(statusModel.IsBacklog)
                {
                    projectData.Backlog = statusData;
                }
                else if(statusModel.IsRelease)
                {
                    releaseStatus = statusData;
                }
                else
                {
                    projectData.Statuses.Add(statusData);
                }
            }
            
            if(releaseStatus != null)
            {
                projectData.Statuses.Add(releaseStatus);
            }
        }

        public static void DeleteById(long id)
        {
            TaskModel.DeleteByStatusId(id);
            Delete(new string[] { "ID = " + id });
        }

        public static void DeleteByProjectId(long id)
        {
            foreach (StatusModel statusModel in Find(new string[] { "ProjectID = " + id }))
            {
                TaskModel.DeleteByStatusId(statusModel.ID);
            }
            Delete(new string[] { "ProjectID = " + id });
        }
    }
}
