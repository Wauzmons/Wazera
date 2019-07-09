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

        public KanbanTaskCard(KanbanBoard kanbanBoard, TaskData data)
        {
            Data = data;
            this.kanbanBoard = kanbanBoard;

            Padding = new Thickness(0);
            BorderThickness = new Thickness(0);
            BorderBrush = Brushes.SkyBlue;

            StackPanel panel = new StackPanel
            {
                Orientation = Orientation.Vertical,
                Margin = new Thickness(3),
                MinHeight = 50,
                MinWidth = 240,
                Background = Brushes.White
            };

            panel.MouseDown += (sender, e) => ItemMouseDown(sender, e);
            PreviewDragOver += (sender, e) => kanbanBoard.ItemPreviewShow(sender, e);
            Drop += (sender, e) => kanbanBoard.ItemDrop(sender, e);
            AllowDrop = true;

            panel.Children.Add(data.GetNameGrid());
            panel.Children.Add(data.GetInfoGrid());
            panel.Children.Add(new Rectangle
            {
                Height = 3,
                Fill = Brushes.Gold
            });
            Content = panel;
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
    }
}
