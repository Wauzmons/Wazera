using System.Collections.Generic;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using Wazera.Data;

namespace Wazera.Project
{
    public partial class ProjectList : Window
    {
        public List<ProjectData> Projects { get; set; }

        private UIElement dialogContent;

        public ProjectList()
        {
            InitializeComponent();

            ProjectData.LoadProjectDatas();
            listView.ItemsSource = new List<ProjectData>(ProjectData.Projects.Values)
                .OrderBy(project => project.Name)
                .ToList();
        }

        public void OpenCreateDialog(ProjectData project)
        {
            CreateProjectDialog createDialog = new CreateProjectDialog(this, project);
            createDialog.closeButton.Click += (sender, e) => CloseCreateDialog();
            dialogContent = createDialog.Content as UIElement;
            createDialog.Content = null;
            createDialog.Close();
            MainWindow.Instance.tgrid.IsEnabled = false;
            listBorder.IsEnabled = false;
            grid.Children.Add(dialogContent);
        }

        public void CloseCreateDialog()
        {
            MainWindow.Instance.tgrid.IsEnabled = true;
            listBorder.IsEnabled = true;
            grid.Children.Remove(dialogContent);
            dialogContent = null;
        }

        public void OpenProject(object sender, MouseButtonEventArgs e)
        {
            if(listView.SelectedValue != null)
            {
                MainWindow.Instance.OpenProjectView(listView.SelectedValue as ProjectData);
            }
        }

        private void Project_MouseEnter(object sender, MouseEventArgs e)
        {
            (sender as Border).Background = new SolidColorBrush(Color.FromArgb(255, 190, 230, 253));
        }

        private void Project_MouseLeave(object sender, MouseEventArgs e)
        {
            (sender as Border).Background = Brushes.White;
        }

        private void Project_MouseRightButtonDown(object sender, MouseButtonEventArgs e)
        {
            ProjectData project = (sender as Border).DataContext as ProjectData;
            OpenCreateDialog(project);
        }
    }
}
