using System.Windows.Controls;
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
            for(int index = 1; index <= 128; index++)
            {
                AddChild(documents.Items, "Test Text-File " + index, "icon_database.png");
            }
            documents.ExpandSubtree();
        }

        public void AddResourceLinks()
        {
            TreeViewItem resources = AddChild(View.Items, "Resource Links", "icon_database.png");
        }

        public TreeViewItem AddChild(ItemCollection parent, string title, string icon)
        {
            StackPanel panel = new StackPanel
            {
                Orientation = Orientation.Horizontal
            };
            Label label = new Label
            {
                Content = title,
                FontSize = 12
            };
            panel.Children.Add(WazeraUtils.GetImage(icon, 16));
            panel.Children.Add(label);

            TreeViewItem item = new TreeViewItem
            {
                Header = panel
            };
            item.Selected += (sender, e) => item.IsSelected = false;
            parent.Add(item);
            return item;
        }
    }
}
