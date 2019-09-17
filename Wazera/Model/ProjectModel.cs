using System.Collections.Generic;
using Wazera.Data;
using WazeraSQL;

namespace Wazera.Model
{
    class ProjectModel : Entity<ProjectModel>
    {
        static ProjectModel() {
            TableName = "Project";
            Columns = new EntityColumn[] {
                new EntityColumn("ID", EntityColumnType.Long, true, true),
                new EntityColumn("ProjectKey", EntityColumnType.String10, true),
                new EntityColumn("Name", EntityColumnType.String50, true),
                new EntityColumn("OwnerID", EntityColumnType.Long, true),
                new EntityColumn("CategoryID", EntityColumnType.Long, true),
                new EntityColumn("LogoString", EntityColumnType.String, false)
            };
            CreateTableIfNotExists();
        }

        public long ID { get; set; }

        public string ProjectKey { get; set; }

        public string Name { get; set; }

        public long OwnerID { get; set; }

        public long CategoryID { get; set; }

        public string LogoString { get; set; }

        public ProjectModel()
        {

        }

        public ProjectModel(ProjectData projectData)
        {
            ID = projectData.ID;
            ProjectKey = projectData.Key;
            Name = projectData.Name;
            OwnerID = projectData.Owner.ID;
            CategoryID = 0;
        }

        public ProjectData ToData()
        {
            return new ProjectData(ProjectKey, Name, LoggedIn.User, CategoryID.ToString())
            {
                ID = ID
            };
        }

        public static List<ProjectData> FindAll()
        {
            List<ProjectData> results = new List<ProjectData>();
            foreach(ProjectModel projectModel in Find(new string[]{ }))
            {
                results.Add(projectModel.ToData());
            }
            return results;
        }

        public static void DeleteById(long id)
        {
            StatusModel.DeleteByProjectId(id);
            Delete(new string[] { "ID = " + id });
        }
    }
}
