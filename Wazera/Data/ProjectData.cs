using System.Collections.Generic;

namespace Wazera.Data
{
    public class ProjectData
    {
        public long ID { get; set; }

        public string Key { get; set; }

        public string Name { get; set; }

        public UserData Owner { get; set; }

        public StatusData Backlog { get; set; }

        public List<StatusData> Statuses { get; set; } = new List<StatusData>();

        public ProjectData(long id, string key, string name, UserData owner)
        {
            ID = id;
            Key = key;
            Name = name;
            Owner = owner;
        }
    }
}
