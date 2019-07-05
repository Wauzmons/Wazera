using System.Windows;
using Wazera.Kanban;

namespace Wazera.Project
{
    public partial class ProjectView : Window
    {
        public ProjectView()
        {
            InitializeComponent();
            SetCenterGridContent(KanbanTester.GetMockBoard());
        }

        public void SetCenterGridContent(Window window)
        {
            UIElement content = window.Content as UIElement;
            window.Content = null;
            window.Close();
            cgrid.Children.Clear();
            cgrid.Children.Add(content);
        }
    }
}
