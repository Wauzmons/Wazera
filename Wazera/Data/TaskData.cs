using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;

namespace Wazera.Data
{
    public class TaskData
    {
        public long ID { get; set; }

        public string Name { get; set; }

        public string Description { get; set; }

        public PriorityData Priority { get; set; }

        public UserData User { get; set; }

        public StatusData Status { get; set; }

        public TaskData(long id, string name, StatusData status, PriorityData priority)
        {
            ID = id;
            Name = name;
            User = LoggedIn.User;
            Status = status;
            Priority = priority;
        }

        public Grid GetNameGrid()
        {
            Grid grid = new Grid();
            grid.Children.Add(GetNameLabel());
            grid.Children.Add(GetKeyLabel(true));
            return grid;
        }

        public Grid GetInfoGrid()
        {
            Grid grid = new Grid();
            grid.Children.Add(Priority.GetPanel());
            grid.Children.Add(User.PanelShortName);
            return grid;
        }

        public Grid GetBacklogGrid(Button button)
        {
            Grid grid = new Grid();
            StackPanel leftPanel = new StackPanel()
            {
                Orientation = Orientation.Horizontal,
                HorizontalAlignment = HorizontalAlignment.Left
            };
            leftPanel.Children.Add(GetKeyLabel(false));
            leftPanel.Children.Add(GetNameLabel());
            grid.Children.Add(leftPanel);

            StackPanel rightPanel = new StackPanel()
            {
                Orientation = Orientation.Horizontal,
                HorizontalAlignment = HorizontalAlignment.Right
            };
            rightPanel.Children.Add(Priority.GetPanel());
            rightPanel.Children.Add(User.PanelShortName);
            rightPanel.Children.Add(button);

            grid.Children.Add(rightPanel);

            return grid;
        }

        public Label GetNameLabel()
        {
            return new Label
            {
                Content = Name,
                HorizontalAlignment = HorizontalAlignment.Left,
                Padding = new Thickness(5),
                ToolTip = new Label
                {
                    Content = "Right Click to Edit"
                }
            };
        }

        public Label GetKeyLabel(bool addMargin)
        {
            return new Label
            {
                Content = new TextBlock
                {
                    Text = GetKey(),
                    TextDecorations = Status.IsRelease
                        ? new TextDecorationCollection(TextDecorations.Strikethrough)
                        : new TextDecorationCollection(),
                    ToolTip = new Label
                    {
                        Content = "Right Click to Edit"
                    }
                },
                HorizontalAlignment = HorizontalAlignment.Right,
                Margin = new Thickness(addMargin ? 10 : 0, 0, 0, 0),
                Padding = new Thickness(5)
            };
        }

        public string GetKey()
        {
            return Status.Project.Key + "-" + ID;
        }
    }
}
