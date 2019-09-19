using System;
using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Animation;
using Wazera.Data;
using Wazera.Project;
using Wazera.Users;
using WazeraSQL;

namespace Wazera
{
    public partial class MainWindow : Window
    {
        public static MainWindow Instance { get; set; }

        private UIElement centerGridContent;

        private RoutedEventHandler plusButtonEventHandler = (sender, e) => { };

        public MainWindow()
        {
            Instance = this;
            InitializeComponent();

            plusIcon.Fill = new ImageBrush(WazeraUtils.GetResource("menu_plus.png"));
            projectsIcon.Fill = new ImageBrush(WazeraUtils.GetResource("menu_projects.png"));
            projectsButton.Click += (sender, e) => OpenProjectList();
            tasksIcon.Fill = new ImageBrush(WazeraUtils.GetResource("menu_tasks.png"));
            usersIcon.Fill = new ImageBrush(WazeraUtils.GetResource("menu_users.png"));

            DataSource.Start();
            OpenLoginScreen();
        }

        public void LoginUser(UserData userData)
        {
            LoggedIn.User = userData;
            userLabel.Content = LoggedIn.User.GetFullName();
            userAvatar.Fill = new ImageBrush(LoggedIn.User.Avatar);

            tgrid.IsEnabled = true;
            OpenProjectList();
        }

        public void OpenLoginScreen()
        {
            LoginScreen loginScreen = new LoginScreen();
            headerLabel.Content = "Welcome to Wazera";
            tgrid.IsEnabled = false;
            SetCenterGridContent(loginScreen);
        }

        public void OpenProjectList()
        {
            ProjectList projectList = new ProjectList();
            headerLabel.Content = "Project List";
            ReplacePlusButtonEventHandler((sender, e) => projectList.OpenCreateDialog(null));
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
            IsEnabled = false;
            UIElement content = window.Content as UIElement;
            window.Content = null;
            window.Close();

            BlendOverlay();

            if (centerGridContent != null)
            {
                cgrid.Children.Remove(centerGridContent);
            }
            centerGridContent = content;
            cgrid.Children.Add(centerGridContent);
            IsEnabled = true;
        }

        public void BlendOverlay()
        {
            overlay.Fill = new SolidColorBrush(Color.FromArgb(100, 255, 255, 255));
            overlay.Fill.BeginAnimation(SolidColorBrush.ColorProperty, new ColorAnimation()
            {
                From = Color.FromArgb(100, 255, 255, 255),
                To = Colors.Transparent,
                Duration = new Duration(TimeSpan.FromMilliseconds(500))
            });
        }

        public void ReplacePlusButtonEventHandler(RoutedEventHandler eventHandler)
        {
            plusButton.Click -= plusButtonEventHandler;
            plusButtonEventHandler = eventHandler;
            plusButton.Click += plusButtonEventHandler;
        }

        private void CloseApplication(object sender, System.EventArgs e)
        {
            Application.Current.Shutdown();
        }
    }
}
