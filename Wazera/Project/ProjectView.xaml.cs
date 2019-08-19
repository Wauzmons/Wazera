using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using Wazera.Data;
using Wazera.Kanban;
using Wazera.Model;

namespace Wazera.Project
{
    public partial class ProjectView : Window
    {
        public ProjectData Data { get; set; }

        private UIElement dialogContent;

        private Button buttonBacklog;
        private Button buttonKanbanBoard;
        private Button buttonReleases;

        public ProjectView(ProjectData data)
        {
            Data = data;
            StatusModel.FillProject(Data);

            InitializeComponent();
            LoadLeftGridContent();
        }

        public void OpenCreateDialog(TaskData task)
        {
            CreateTaskDialog createDialog = new CreateTaskDialog(this, task);
            createDialog.saveButton.Click += (sender, e) => CloseCreateDialog();
            createDialog.closeButton.Click += (sender, e) => CloseCreateDialog();
            dialogContent = createDialog.Content as UIElement;
            createDialog.Content = null;
            createDialog.Close();
            MainWindow.Instance.tgrid.IsEnabled = false;
            dockPanel.IsEnabled = false;
            grid.Children.Add(dialogContent);
        }

        public void CloseCreateDialog()
        {
            MainWindow.Instance.tgrid.IsEnabled = true;
            dockPanel.IsEnabled = true;
            grid.Children.Remove(dialogContent);
            dialogContent = null;
        }

        public void SetCenterGridContent(Window window)
        {
            UIElement content = window.Content as UIElement;
            window.Content = null;
            window.Close();
            cgrid.Children.Clear();
            cgrid.Children.Add(content);
        }

        private void LoadLeftGridContent()
        {
            buttonBacklog = AddMenuButton("Backlog", "proj_backlog.png");
            buttonBacklog.Click += (sender, e) => MainWindow.Instance.BlendOverlay();
            buttonBacklog.Click += (sender, e) => BacklogButtonClick();

            buttonKanbanBoard = AddMenuButton("Kanban Board", "proj_board.png");
            buttonKanbanBoard.Click += (sender, e) => MainWindow.Instance.BlendOverlay();
            buttonKanbanBoard.Click += (sender, e) => KanbanBoardButtonClick();

            buttonReleases = AddMenuButton("Releases", "proj_releases.png");
            buttonReleases.Click += (sender, e) => MainWindow.Instance.BlendOverlay();
            buttonReleases.Click += (sender, e) => ReleasesButtonClick();

            new ProjectTreeView(Data, treeView);

            KanbanBoardButtonClick();
        }

        private Button AddMenuButton(string displayName, string iconName)
        {
            StackPanel panel = new StackPanel()
            {
                HorizontalAlignment = HorizontalAlignment.Stretch,
                Orientation = Orientation.Horizontal,
            };
            panel.Children.Add(new Image
            {
                Source = WazeraUtils.GetResource(iconName),
                Margin = new Thickness(5),
                Width = 24,
                Height = 24
            });
            panel.Children.Add(new Label
            {
                HorizontalAlignment = HorizontalAlignment.Right,
                Margin = new Thickness(5),
                Content = displayName,
                FontSize = 18,
                FontWeight = FontWeights.Bold,
                Foreground = Brushes.DarkSlateGray
            });
            Button button = new Button
            {
                HorizontalAlignment = HorizontalAlignment.Stretch,
                HorizontalContentAlignment = HorizontalAlignment.Left,
                Margin = new Thickness(5, 5, 5, 0),
                Content = panel,
                Background = Brushes.LightGray,
                BorderBrush = Brushes.SkyBlue,
                BorderThickness = new Thickness(0)
            };
            projectPanel.Children.Add(button);
            return button;
        }

        public void BacklogButtonClick()
        {
            HighlightButton(buttonBacklog);
            SetCenterGridContent(new KanbanBoard(Data, this, true));
        }

        public void KanbanBoardButtonClick()
        {
            HighlightButton(buttonKanbanBoard);
            SetCenterGridContent(new KanbanBoard(Data, this, false));
        }

        public void ReleasesButtonClick()
        {
            HighlightButton(buttonReleases);
            SetCenterGridContent(new KanbanBoard(Data, this, false));
        }

        public void HighlightButton(Button button)
        {
            buttonBacklog.BorderThickness = new Thickness(0);
            buttonKanbanBoard.BorderThickness = new Thickness(0);
            buttonReleases.BorderThickness = new Thickness(0);
            if(button != null)
            {
                button.BorderThickness = new Thickness(0, 0, 15, 0);
            }
        }
    }
}
