using System;
using System.ComponentModel;
using System.Reflection;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using Wazera.Data;
using Wazera.Project;

namespace Wazera
{
    public partial class MainWindow : Window
    {
        public static MainWindow Instance { get; set; }

        private RoutedEventHandler plusButtonEventHandler = (sender, e) => { };

        public MainWindow()
        {
            Instance = this;
            InitializeComponent();

            projectsButton.Click += (sender, e) => OpenProjectList();

            LoginUser(WazeraTester.GetMockUser());
            OpenProjectView(WazeraTester.GetMockProject());
        }

        public void LoginUser(UserData userData)
        {
            LoggedIn.User = userData;
            userLabel.Content = LoggedIn.User.GetFullName();
            userAvatar.Fill = new ImageBrush(LoggedIn.User.Avatar);
        }

        public void OpenProjectList()
        {
            ProjectList projectList = new ProjectList();
            headerLabel.Content = "Project List";
            ReplacePlusButtonEventHandler((sender, e) => { });
            SetCenterGridContent(projectList);
        }

        public void OpenProjectView(ProjectData data)
        {
            ProjectView projectView = new ProjectView(data);
            headerLabel.Content = projectView.Data.Name;
            ReplacePlusButtonEventHandler((sender, e) => projectView.OpenCreateDialog(null));
            SetCenterGridContent(projectView);
        }

        public void SetCenterGridContent(Window window)
        {
            UIElement content = window.Content as UIElement;
            window.Content = null;
            window.Close();
            cgrid.Children.Clear();
            cgrid.Children.Add(content);
        }

        public void ReplacePlusButtonEventHandler(RoutedEventHandler eventHandler)
        {
            plusButton.Click -= plusButtonEventHandler;
            plusButtonEventHandler = eventHandler;
            plusButton.Click += plusButtonEventHandler;
        }
    }
}
