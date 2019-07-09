using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;
using Wazera.Data;

namespace Wazera.Project
{
    public partial class CreateTaskDialog : Window
    {
        public ProjectData Data { get; set; }

        public CreateTaskDialog(ProjectData data)
        {
            Data = data;

            InitializeComponent();
            AddPriorities();
            AddUsers();
            AddStatuses();
        }

        public void AddPriorities()
        {
            foreach (PriorityData priority in PriorityData.Priorities)
            {
                StackPanel panel = priority.GetPanel();
                panel.Margin = new Thickness(0);
                priorityInput.Items.Add(new ComboBoxItem
                {
                    Content = panel
                });
            }
            priorityInput.SelectedIndex = 2;
        }

        public void AddUsers()
        {
            StackPanel panel = LoggedIn.User.GetPanel(true);
            panel.Margin = new Thickness(0);
            userInput.Items.Add(new ComboBoxItem
            {
                Content = panel
            });
            userInput.SelectedIndex = 0;
        }

        public void AddStatuses()
        {
            List<StatusData> statuses = new List<StatusData>();
            statuses.Add(Data.Backlog);
            statuses.AddRange(Data.Statuses);
            foreach (StatusData status in statuses)
            {
                statusInput.Items.Add(new ComboBoxItem
                {
                    Content = new Label
                    {
                        Content = status.Title + " (" + status.Tasks.Count + ")"
                    }
                });
            }
            statusInput.SelectedIndex = 0;
        }
    }
}
