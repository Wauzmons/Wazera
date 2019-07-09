using System.Collections.Generic;

namespace Wazera.Data
{
    public class StatusData
    {
        public string Title { get; set; }

        public int MinCards { get; set; }

        public int MaxCards { get; set; }

        public List<TaskData> Tasks { get; set; } = new List<TaskData>();

        public ProjectData Project { get; set; }

        public StatusData(string title, ProjectData project)
        {
            Title = title;
            Project = project;
            MinCards = 0;
            MaxCards = 0;
        }

        public StatusData(string title, ProjectData project, int minCards, int maxCards)
        {
            Title = title;
            Project = project;
            MinCards = minCards;
            MaxCards = maxCards;
        }

        public bool HasCardMinimum()
        {
            return MinCards != 0;
        }

        public bool HasCardMaximum()
        {
            return MaxCards != 0;
        }
    }
}
