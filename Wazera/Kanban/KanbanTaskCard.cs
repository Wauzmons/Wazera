using System;
using System.Runtime.InteropServices;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Shapes;
using Wazera.Data;
using Wazera.Model;

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

            Margin = new Thickness(0);
            Padding = new Thickness(3);
            BorderThickness = new Thickness(0);
            BorderBrush = Brushes.SkyBlue;
            Selected += (sender, e) => IsSelected = false;

            panel = new StackPanel
            {
                Orientation = Orientation.Vertical,
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

                    new TaskModel(Data).Save();
                };
                panel.Children.Add(data.GetBacklogGrid(button));
            }
            else
            {
                panel.Children.Add(data.GetNameGrid());
                panel.Children.Add(data.GetInfoGrid());
                panel.PreviewMouseLeftButtonDown += (sender, e) => ItemMouseDown(sender, e);
                PreviewDragOver += (sender, e) => kanbanBoard.ItemPreviewShow(sender, e);
                GiveFeedback += (sender, e) => DragDropWindowUpdate();
                Drop += (sender, e) => kanbanBoard.ItemDrop(sender, e);
                AllowDrop = true;
            }
            panel.Children.Add(GetColorRectangle());
            Content = panel;
        }

        public Rectangle GetColorRectangle()
        {
            Brush colorBrush;
            if(Data.Status.IsRelease)
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
            DragDropWindowShow();
            Visibility = Visibility.Collapsed;
            DragDrop.DoDragDrop(this, this, DragDropEffects.All);
            Visibility = Visibility.Visible;
            kanbanBoard.DragDropWindowRemove();
            kanbanBoard.ItemPreviewRemove();
        }

        private void DragDropWindowShow()
        {
            kanbanBoard.DragDropWindowRemove();
            kanbanBoard.DragDropWindow = new Window
            {
                WindowStyle = WindowStyle.None,
                AllowsTransparency = true,
                AllowDrop = false,
                Background = null,
                IsHitTestVisible = false,
                SizeToContent = SizeToContent.WidthAndHeight,
                Topmost = true,
                ShowInTaskbar = false
            };

            VisualBrush brush = new VisualBrush(new KanbanTaskCard(kanbanBoard, Data));
            Rectangle rectangle = new Rectangle
            {
                Width = ActualWidth,
                Height = ActualHeight,
                Fill = brush,
                LayoutTransform = new RotateTransform(10)
            };
            kanbanBoard.DragDropWindow.Content = rectangle;

            DragDropWindowUpdate();
            kanbanBoard.DragDropWindow.Show();
        }

        private void DragDropWindowUpdate()
        {
            if(kanbanBoard.DragDropWindow == null)
            {
                return;
            }

            Win32Point win32MousePoint = new Win32Point();
            GetCursorPos(ref win32MousePoint);

            kanbanBoard.DragDropWindow.Left = win32MousePoint.X;
            kanbanBoard.DragDropWindow.Top = win32MousePoint.Y;
        }

        [DllImport("user32.dll")]
        [return: MarshalAs(UnmanagedType.Bool)]
        internal static extern bool GetCursorPos(ref Win32Point pt);

        [StructLayout(LayoutKind.Sequential)]
        internal struct Win32Point
        {
            public Int32 X;
            public Int32 Y;
        }

        public void SetDefaultBrush(Brush defaultBrush)
        {
            this.defaultBrush = defaultBrush;
            panel.Background = defaultBrush;
        }
    }
}
