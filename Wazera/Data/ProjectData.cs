using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using Wazera.Model;

namespace Wazera.Data
{
    public class ProjectData
    {
        public static Dictionary<string, ProjectData> Projects { get; } = new Dictionary<string, ProjectData>();

        public long ID { get; set; }

        public string Key { get; set; }

        public string Name { get; set; }

        public UserData Owner { get; set; }

        public CategoryData Category { get; set; }

        public StatusData Backlog { get; set; }

        public List<StatusData> Statuses { get; set; } = new List<StatusData>();

        public BitmapImage Logo { get; set; }

        public ProjectData(string key, string name, UserData owner, CategoryData category)
        {
            Key = key;
            Name = name;
            Owner = owner;
            Category = category;
        }

        public static void LoadProjectDatas()
        {
            Projects.Clear();
            foreach(ProjectData project in ProjectModel.FindAll())
            {
                Projects.Add(project.Key, project);
            }
        }

        public List<StatusData> GetAllStatuses()
        {
            List<StatusData> statuses = new List<StatusData>
            {
                Backlog
            };
            statuses.AddRange(Statuses);
            return statuses;
        }

        public StackPanel PanelShortName { get { return GetPanel(false); } }

        public StackPanel PanelFullName { get { return GetPanel(true); } }

        private StackPanel GetPanel(bool showFullName)
        {
            StackPanel panel = new StackPanel
            {
                HorizontalAlignment = HorizontalAlignment.Right,
                Orientation = Orientation.Horizontal,
                Margin = new Thickness(5),
                ToolTip = new Label
                {
                    Content = "Project [" + Name + "]"
                }
            };
            panel.Children.Add(GetLogoEllipse(18));
            panel.Children.Add(new Label
            {
                HorizontalAlignment = HorizontalAlignment.Right,
                Content = showFullName ? Name : Key
            });
            return panel;
        }

        public Ellipse GetLogoEllipse(int diameter)
        {
            return new Ellipse
            {
                HorizontalAlignment = HorizontalAlignment.Right,
                Width = diameter,
                Height = diameter,
                Fill = new ImageBrush(Logo ?? WazeraUtils.GetResource("default_project.png")),
                Stroke = Brushes.LightSlateGray,
                StrokeThickness = 1
            };
        }
    }
}
