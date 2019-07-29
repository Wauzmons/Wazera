using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Shapes;
using Wazera.Data;

namespace Wazera.Project
{
    class ProjectTreeView
    {
        public ProjectData Data { get; set; }

        public TreeView View { get; set; }

        public ProjectTreeView(ProjectData data, TreeView view)
        {
            Data = data;
            View = view;

            AddDocuments();
            AddResourceLinks();
        }

        public void AddDocuments()
        {
            TreeViewItem documents = AddChild(View.Items, "Documents", "icon_database.png");
            for (int index = 1; index <= 128; index++)
            {
                AddChild(documents.Items, "Test Text-File " + index, "icon_database.png");
            }
        }

        public void AddResourceLinks()
        {
            TreeViewItem resources = AddChild(View.Items, "Resource Links", "icon_database.png");
        }

        public TreeViewItem AddChild(ItemCollection parent, string title, string icon)
        {
            StackPanel verticalPanel = new StackPanel
            {
                Orientation = Orientation.Vertical,
                Background = Brushes.LightGray
            };
            verticalPanel.MouseEnter += (sender, e) => verticalPanel.Background = new SolidColorBrush(Color.FromArgb(255, 190, 230, 253));
            verticalPanel.MouseLeave += (sender, e) => verticalPanel.Background = Brushes.LightGray;

            StackPanel horizontalPanel = new StackPanel
            {
                Orientation = Orientation.Horizontal
            };
            verticalPanel.Children.Add(horizontalPanel);
            verticalPanel.Children.Add(new Rectangle
            {
                Height = 3,
                Fill = Brushes.LightSkyBlue
            });

            Label label = new Label
            {
                Content = title,
                Padding = new Thickness(5),
                FontWeight = FontWeights.DemiBold
            };
            horizontalPanel.Children.Add(WazeraUtils.GetImage(icon, 16));
            horizontalPanel.Children.Add(label);

            TreeViewItem item = new TreeViewItem
            {
                Header = verticalPanel,
                HorizontalAlignment = HorizontalAlignment.Stretch,
                HorizontalContentAlignment = HorizontalAlignment.Stretch,
                Padding = new Thickness(3)
            };
            item.Selected += (sender, e) => item.IsSelected = false;
            parent.Add(item);
            return item;
        }
    }
}
