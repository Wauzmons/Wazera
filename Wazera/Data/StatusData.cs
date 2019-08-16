using System.Collections.Generic;
using System.Windows.Controls;

namespace Wazera.Data
{
    public class StatusData
    {
        public long ID { get; set; }

        public string Title { get; set; }

        public int MinCards { get; set; }

        public int MaxCards { get; set; }

        public bool IsBacklog { get; set; } = false;

        public bool IsRelease { get; set; } = false;

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

        public StatusData(string title, ProjectData project, bool isBacklog, bool isRelease, int minCards, int maxCards)
        {
            Title = title;
            Project = project;
            MinCards = minCards;
            MaxCards = maxCards;
            IsBacklog = isBacklog;
            IsRelease = isRelease;
        }

        public bool HasCardMinimum()
        {
            return MinCards != 0;
        }

        public bool HasCardMaximum()
        {
            return MaxCards != 0;
        }

        public Label GetLabel()
        {
            return new Label
            {
                Content = Title + " (" + Tasks.Count + ")"
            };
        }
    }
}
