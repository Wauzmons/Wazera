using System.Windows;
using System.Windows.Controls;

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
            Label nameLabel = new Label
            {
                Content = Name,
                HorizontalAlignment = HorizontalAlignment.Left,
                Padding = new Thickness(5)
            };
            Label keyLabel = new Label
            {
                Content = Status.Project.Key + "-" + ID,
                HorizontalAlignment = HorizontalAlignment.Right,
                Margin = new Thickness(10, 0, 0, 0),
                Padding = new Thickness(5)
            };
            grid.Children.Add(nameLabel);
            grid.Children.Add(keyLabel);
            return grid;
        }

        public Grid GetInfoGrid()
        {
            Grid grid = new Grid();
            grid.Children.Add(Priority.GetPanel());
            grid.Children.Add(User.GetPanel());
            return grid;
        }
    }
}
