using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Shapes;
using Wazera.Data;

namespace Wazera.Kanban
{
    public class KanbanTaskCard : ListViewItem
    {
        public TaskData Data { get; set; }

        private KanbanBoard kanbanBoard;
        private StackPanel panel;
        private Brush defaultBrush = Brushes.White;

        public KanbanTaskCard(KanbanBoard kanbanBoard, TaskData data)
        {
            Data = data;
            this.kanbanBoard = kanbanBoard;

            Padding = new Thickness(0);
            BorderThickness = new Thickness(0);
            BorderBrush = Brushes.SkyBlue;
            Selected += (sender, e) => IsSelected = false;

            panel = new StackPanel
            {
                Orientation = Orientation.Vertical,
                Margin = new Thickness(3),
                MinHeight = 20,
                MinWidth = 240,
                Background = Brushes.White
            };
            panel.MouseEnter += (sender, e) => panel.Background = new SolidColorBrush(Color.FromArgb(255, 190, 230, 253));
            panel.MouseLeave += (sender, e) => panel.Background = defaultBrush;
            panel.MouseRightButtonDown += (sender, e) => kanbanBoard.View.OpenCreateDialog(Data);

            if (Data.Status.IsBacklog)
            {
                Button button = new Button
                {
                    Content = "Send to Board",
                    Margin = new Thickness(5),
                    Padding = new Thickness(5, 0, 5, 0),
                    Background = Brushes.LightSkyBlue
                };
                button.Click += (sender, e) =>
                {
                    KanbanColumn column = Parent as KanbanColumn;
                    column.Items.Remove(this);
                    column.UpdateHeader();

                    ProjectData project = Data.Status.Project;
                    StatusData newStatus = project.Statuses[0];

                    Data.Status.Tasks.Remove(Data);
                    newStatus.Tasks.Add(Data);
                    Data.Status = newStatus;
                };
                panel.Children.Add(data.GetBacklogGrid(button));
            }
            else
            {
                panel.Children.Add(data.GetNameGrid());
                panel.Children.Add(data.GetInfoGrid());
                panel.MouseDown += (sender, e) => ItemMouseDown(sender, e);
                PreviewDragOver += (sender, e) => kanbanBoard.ItemPreviewShow(sender, e);
                Drop += (sender, e) => kanbanBoard.ItemDrop(sender, e);
                AllowDrop = true;
            }
            panel.Children.Add(GetColorRectangle());
            Content = panel;
        }

        public Rectangle GetColorRectangle()
        {
            Brush colorBrush;
            if(Data.Status.IsBacklog)
            {
                colorBrush = Brushes.CornflowerBlue;
            }
            else if(Data.Status.IsRelease)
            {
                colorBrush = Brushes.LimeGreen;
            }
            else
            {
                colorBrush = Brushes.Gold;
            }
            return new Rectangle
            {
                Height = 3,
                Fill = colorBrush
            };
        }

        private void ItemMouseDown(object sender, MouseEventArgs e)
        {
            EnableHighlight();
            DragDrop.DoDragDrop(this, this, DragDropEffects.All);
            kanbanBoard.ItemPreviewRemove();
            DisableHighlight();
        }

        private void EnableHighlight()
        {
            Padding = new Thickness(0);
            BorderThickness = new Thickness(3);
        }

        private void DisableHighlight()
        {
            Padding = new Thickness(0);
            BorderThickness = new Thickness(0);
        }

        public void SetDefaultBrush(Brush defaultBrush)
        {
            this.defaultBrush = defaultBrush;
            panel.Background = defaultBrush;
        }
    }
}
