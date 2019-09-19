using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using Wazera.Data;
using Wazera.Kanban;
using Wazera.Model;

namespace Wazera.Project
{
    public partial class CreateProjectDialog : Window
    {
        public ProjectList Projects;
        public ProjectData Project;

        public static string BacklogDescription { get; } = "Shows tasks, that have been put back for future sprints";
        public static string PlannedDescription { get; } = "Shows tasks, that are selected for development";
        public static string ReleaseDescription { get; } = "Shows finished tasks, ready for the next release";

        public CreateProjectDialog(ProjectList projects, ProjectData project)
        {
            Projects = projects;
            Project = project;

            InitializeComponent();
            if(Project != null)
            {
                StatusModel.FillProject(Project);
                headerLabel.Content = "Edit Project     " + Project.Key;
                deleteButton.IsEnabled = true;

                nameInput.Text = Project.Name;
                keyInput.Text = Project.Key;

                KanbanColumnOptions releaseColumn = null;
                List<StatusData> statuses = Project.GetAllStatuses();
                foreach (StatusData status in statuses)
                {
                    bool editable = !status.IsBacklog;
                    string title = status.Title;

                    string description = null;
                    if(status.IsBacklog)
                    {
                        description = BacklogDescription;
                    }
                    else if(statuses.IndexOf(status) == 1)
                    {
                        description = PlannedDescription;
                    }
                    else if(status.IsRelease)
                    {
                        description = ReleaseDescription;
                    }

                    KanbanColumnOptions columnOptions = new KanbanColumnOptions(editable, title, description)
                    {
                        ID = status.ID
                    };
                    columnOptions.SetMinCards(status.MinCards);
                    columnOptions.SetMaxCards(status.MaxCards);
                    if(status.IsRelease)
                    {
                        releaseColumn = columnOptions;
                    }
                    else
                    {
                        columnList.Children.Add(columnOptions);
                    }
                }
                columnList.Children.Add(releaseColumn);
            }
            else
            {
                columnList.Children.Add(new KanbanColumnOptions(false, "Backlog", BacklogDescription));
                columnList.Children.Add(new KanbanColumnOptions(true, "Planned", PlannedDescription));
                columnList.Children.Add(new KanbanColumnOptions(true, "Done", ReleaseDescription));
            }

            ImageBrush logoBrush = new ImageBrush(WazeraUtils.GetResource("default_project.png"));
            logoPreview.Fill = logoBrush;
            logoPreviewSmall.Fill = logoBrush;

            AddCategories();

            nameInput.TextChanged += (sender, e) => GenerateKey();
            keyInput.TextChanged += (sender, e) => FormatKey();
            saveButton.Click += (sender, e) => SaveButtonClick();
        }

        public void AddCategories()
        {
            categoryInput.SelectedIndex = 0;
            foreach (CategoryData category in CategoryData.GetAllCategories())
            {
                StackPanel panel = category.GetPanel();
                panel.Margin = new Thickness(0);
                ComboData<CategoryData> item = new ComboData<CategoryData>(category)
                {
                    Content = panel
                };
                categoryInput.Items.Add(item);
                if (Project != null && Project.Category.ID == category.ID)
                {
                    categoryInput.SelectedIndex = categoryInput.Items.Count - 1;
                }
            }
        }

        public void GenerateKey()
        {
            keyInput.Text = string.Concat(Regex
                .Matches(nameInput.Text, "[A-Z0-9]")
                .OfType<Match>()
                .Select(match => match.Value));

            FormatKey();
        }

        public void FormatKey()
        {
            int caretPosition = keyInput.SelectionStart;

            string key = keyInput.Text.ToUpper().Replace(" ", "");
            keyInput.Text = key.Length <= keyInput.MaxLength ? key : key.Substring(0, keyInput.MaxLength);

            keyInput.SelectionStart = caretPosition;
        }

        private void InsertNewColumn(object sender, RoutedEventArgs e)
        {
            KanbanColumnOptions column = new KanbanColumnOptions(true, "New Column", null);
            column.Background = new SolidColorBrush(Color.FromArgb(255, 230, 255, 230));
            columnList.Children.Insert(columnList.Children.Count - 1, column);
            scrollViewer.ScrollToBottom();
        }

        private void SaveButtonClick()
        {
            string name = string.IsNullOrWhiteSpace(nameInput.Text) ? "Unnamed Project" : nameInput.Text;
            string key = string.IsNullOrWhiteSpace(keyInput.Text) ? "PROJ" : keyInput.Text;

            ComboData<CategoryData> categorySelection = categoryInput.SelectedItem as ComboData<CategoryData>;
            CategoryData category = categorySelection.Value;

            UserData owner = LoggedIn.User;

            if(Project != null)
            {
                Project.Key = key;
                Project.Name = name;
                Project.Owner = owner;
                Project.Category = category;
                new ProjectModel(Project).Save();
            }
            else
            {
                Project = new ProjectData(key, name, owner, category);
                Project.ID = new ProjectModel(Project).Save();
            }

            foreach (KanbanColumnOptions column in columnList.Children)
            {
                bool isBacklog = !column.Editable;
                bool isRelease = columnList.Children[columnList.Children.Count - 1].Equals(column);
                string title = column.GetTitle();
                int minCards = column.GetMinCards();
                int maxCards = column.GetMaxCards();
                StatusData status = new StatusData(title, Project, isBacklog, isRelease, minCards, maxCards)
                {
                    ID = column.ID
                };
                new StatusModel(status).Save();
            }

            Projects.CloseDialog();
            MainWindow.Instance.OpenProjectView(Project);
        }

        private void DeleteButtonClick(object sender, RoutedEventArgs e)
        {
            MessageBoxResult result = MessageBox.Show("Do you really want to delete \'" + Project.Name + "\'?", "Confirmation", MessageBoxButton.OKCancel);
            if(result == MessageBoxResult.OK)
            {
                ProjectModel.DeleteById(Project.ID);
                Projects.CloseDialog();
                MainWindow.Instance.OpenProjectList();
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
