using System.Linq;
using System.Text.RegularExpressions;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using Wazera.Data;
using Wazera.Data.Model;
using Wazera.Kanban;

namespace Wazera.Project
{
    public partial class CreateProjectDialog : Window
    {
        public ProjectList Projects;

        public CreateProjectDialog(ProjectList projects)
        {
            Projects = projects;

            InitializeComponent();

            ImageBrush logoBrush = new ImageBrush(WazeraUtils.GetResource("default_project.png"));
            logoPreview.Fill = logoBrush;
            logoPreviewSmall.Fill = logoBrush;

            columnList.Children.Add(new KanbanColumnOptions(false, "Backlog", "Recorded tasks for future sprints land here"));
            columnList.Children.Add(new KanbanColumnOptions(true, "Planned", "Shows tasks selected for development"));
            columnList.Children.Add(new KanbanColumnOptions(true, "Done", "Finished tasks, ready for the next release"));

            nameInput.TextChanged += (sender, e) => GenerateKey();
            keyInput.TextChanged += (sender, e) => FormatKey();
            saveButton.Click += (sender, e) => SaveButtonClick();
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
            columnList.Children.Insert(columnList.Children.Count - 1, column);
            scrollViewer.ScrollToBottom();
        }

        public void SaveButtonClick()
        {
            string name = string.IsNullOrWhiteSpace(nameInput.Text) ? "Unnamed Project" : nameInput.Text;
            string category = string.IsNullOrWhiteSpace(categoryInput.Text) ? "Unspecified" : categoryInput.Text;

            string key = keyInput.Text;
            if(string.IsNullOrWhiteSpace(key) || ProjectData.Projects.ContainsKey(key))
            {
                MessageBox.Show("Key must be unique!");
                return;
            }

            UserData owner = LoggedIn.User;

            ProjectData project = new ProjectData(key, name, owner, category);
            project.Backlog = new StatusData("Backlog", project, true, false, 0, 0);
            foreach(KanbanColumnOptions column in columnList.Children)
            {
                if(!column.Editable)
                {
                    continue;
                }
                bool isRelease = columnList.Children[columnList.Children.Count - 1].Equals(column);
                string title = column.GetTitle();
                int minCards = column.GetMinCards();
                int maxCards = column.GetMaxCards();
                project.Statuses.Add(new StatusData(title, project, false, isRelease, minCards, maxCards));
            }
            new ProjectModel(project).Save();
            MainWindow.Instance.OpenProjectView(project);
            Projects.CloseCreateDialog();
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
