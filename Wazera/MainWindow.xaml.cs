using System.Windows;

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
            Kanban kanban = new Kanban();
            kanban.Show();
        }
    }
}
