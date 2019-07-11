using System.Windows;
using System.Windows.Controls;
using Wazera.Data;

namespace Wazera.Project
{
    public partial class CreateTaskDialog : Window
    {
        public ProjectView Project { get; set; }

        public TaskData Task { get; set; }

        public CreateTaskDialog(ProjectView project, TaskData task)
        {
            Project = project;
            Task = task;

            InitializeComponent();
            if(task != null)
            {
                headerLabel.Content = "Edit Task   " + Task.GetKey();

                nameInput.Text = Task.Name;
                descriptionInput.Text = Task.Description;
            }
            AddPriorities();
            AddUsers();
            AddStatuses();

            saveButton.Click += (sender, e) => SaveButtonClick();
        }

        public void AddPriorities()
        {
            priorityInput.SelectedIndex = 2;
            foreach (PriorityData priority in PriorityData.Priorities)
            {
                StackPanel panel = priority.GetPanel();
                panel.Margin = new Thickness(0);
                ComboData<PriorityData> item = new ComboData<PriorityData>(priority)
                {
                    Content = panel
                };
                priorityInput.Items.Add(item);
                if(Task != null && Task.Priority.ID == priority.ID)
                {
                    priorityInput.SelectedIndex = priorityInput.Items.Count - 1;
                }
            }
        }

        public void AddUsers()
        {
            userInput.SelectedIndex = 0;
            for (int index = 0; index < 1; index++) // #ToDo Placeholder
            {
                StackPanel panel = LoggedIn.User.PanelFullName;
                panel.Margin = new Thickness(0);
                ComboData<UserData> item = new ComboData<UserData>(LoggedIn.User)
                {
                    Content = panel
                };
                userInput.Items.Add(item);
            }
        }

        public void AddStatuses()
        {
            statusInput.SelectedIndex = 0;
            foreach (StatusData status in Project.Data.GetAllStatuses())
            {
                Label label = status.GetLabel();
                label.Margin = new Thickness(0);
                ComboData<StatusData> item = new ComboData<StatusData>(status)
                {
                    Content = label
                };
                statusInput.Items.Add(item);
                if (Task != null && Task.Status.Title == status.Title)
                {
                    statusInput.SelectedIndex = statusInput.Items.Count - 1;
                }
            }
        }

        public void SaveButtonClick()
        {
            string name = nameInput.Text;
            string description = descriptionInput.Text;

            ComboData<PriorityData> prioritySelection = priorityInput.SelectedItem as ComboData<PriorityData>;
            PriorityData priority = prioritySelection.Value;

            ComboData<UserData> userSelection = userInput.SelectedItem as ComboData<UserData>;
            UserData user = userSelection.Value;

            ComboData<StatusData> statusSelection = statusInput.SelectedItem as ComboData<StatusData>;
            StatusData status = statusSelection.Value;

            if(Task != null)
            {
                Task.Name = name;
                Task.Description = description;
                Task.Priority = priority;
                Task.User = user;

                StatusData oldStatus = Task.Status;
                StatusData newStatus = status;
                if(!oldStatus.Title.Equals(newStatus.Title))
                {
                    oldStatus.Tasks.Remove(Task);
                    newStatus.Tasks.Add(Task);
                    Task.Status = status;
                }
            }
            else
            {
                TaskData task = new TaskData(666, name, status, priority)
                {
                    Description = description,
                    User = user
                };
                status.Tasks.Insert(0, task);
            }

            if (status.IsBacklog)
            {
                Project.BacklogButtonClick();
            }
            else
            {
                Project.KanbanBoardButtonClick();
            }
        }
    }
}
