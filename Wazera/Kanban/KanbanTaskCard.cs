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
        private TaskData data;

        private KanbanBoard kanbanBoard;
        private StackPanel panel;
        private Label label;

        public KanbanTaskCard(KanbanBoard kanbanBoard, TaskData data)
        {
            this.data = data;
            this.kanbanBoard = kanbanBoard;

            Padding = new Thickness(0);
            BorderThickness = new Thickness(0);
            BorderBrush = Brushes.SkyBlue;

            panel = new StackPanel
            {
                Orientation = Orientation.Vertical,
                Margin = new Thickness(3),
                MinHeight = 50,
                MinWidth = 250,
                Background = Brushes.White
            };

            label = new Label
            {
                Content = data.Name,
                Padding = new Thickness(5),
            };

            Rectangle rect = new Rectangle
            {
                Height = 3,
                Fill = Brushes.Gold
            };

            panel.MouseDown += (sender, e) => ItemMouseDown(sender, e);
            DragOver += (sender, e) => kanbanBoard.ItemPreviewShow(sender, e);
            Drop += (sender, e) => kanbanBoard.ItemDrop(sender, e);
            AllowDrop = true;

            panel.Children.Add(label);
            panel.Children.Add(data.Priority.GetPanel());
            panel.Children.Add(rect);
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
