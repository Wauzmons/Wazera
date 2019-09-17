using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using Wazera.Data;
using Wazera.Model;

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
                headerLabel.Content = "Edit Task     " + Task.GetKey();
                deleteButton.IsEnabled = true;

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

        private void SaveButtonClick()
        {
            string name = string.IsNullOrWhiteSpace(nameInput.Text) ? "Unnamed Task" : nameInput.Text;
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
                Task.Status = status;
                new TaskModel(Task).Save();
            }
            else
            {
                Task = new TaskData(name, description, priority, user, status);
                Task.ID = new TaskModel(Task).Save();
            }

            Project.CloseCreateDialog();
            OpenProjectView();
        }

        private void DeleteButtonClick(object sender, RoutedEventArgs e)
        {
            MessageBoxResult result = MessageBox.Show("Do you really want to delete \'" + Task.Name + "\'?", "Confirmation", MessageBoxButton.OKCancel);
            if (result == MessageBoxResult.OK)
            {
                TaskModel.DeleteById(Task.ID);
                Project.CloseCreateDialog();
                OpenProjectView();
            }
        }

        private void OpenProjectView()
        {
            if (Task.Status.IsBacklog)
            {
                Project.BacklogButtonClick();
            }
            else
            {
                Project.KanbanBoardButtonClick();
            }
        }

        private void ScrollViewerOnPreviewMouseWheel(object sender, MouseWheelEventArgs e)
        {
            var scv = sender as ScrollViewer;
            if (scv == null) return;
            scv.ScrollToVerticalOffset(scv.VerticalOffset - e.Delta);
            e.Handled = true;
        }
    }
}
