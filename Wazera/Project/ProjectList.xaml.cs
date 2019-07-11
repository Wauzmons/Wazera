using System.Collections.Generic;
using System.Linq;
using System.Windows;
using Wazera.Data;

namespace Wazera.Project
{
    public partial class ProjectList : Window
    {
        public List<ProjectData> Projects { get; set; }

        public ProjectList()
        {
            InitializeComponent();
            listView.ItemsSource = new List<ProjectData>(ProjectData.Projects.Values)
                .OrderBy(project => project.Name)
                .ToList();
        }
    }
}
