using System.Windows;
using Wazera.Project;

namespace Wazera
{
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            new ProjectView(WazeraTester.GetMockProject()).Show();
            Close();
        }
    }
}
