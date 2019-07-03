using System.Windows;
using Wazera.Kanban;

namespace Wazera.Project
{
    public partial class ProjectView : Window
    {
        public ProjectView()
        {
            InitializeComponent();
            OpenKanbanBoard(new KanbanBoard());
        }

        public void OpenKanbanBoard(KanbanBoard kanbanBoard)
        {
            UIElement content = kanbanBoard.Content as UIElement;
            kanbanBoard.Content = null;
            cgrid.Children.Add(content);
        }
    }
}
