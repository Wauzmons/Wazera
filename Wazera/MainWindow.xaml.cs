using System.Windows;
using Wazera.Kanban;
using Wazera.Project;

namespace Wazera
{
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
        }

        public void OpenKanbanBoard(object sender, RoutedEventArgs e)
        {
            KanbanTester.GetMockBoard().Show();
        }

        public void OpenProjectView(object sender, RoutedEventArgs e)
        {
            ProjectView projectView = new ProjectView();
            projectView.Owner = this;
            projectView.ShowDialog();
        }
    }
}
