using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;

namespace Wazera.Kanban
{
    class KanbanTaskCard : ListViewItem
    {
        private KanbanBoard kanbanBoard;
        private StackPanel panel;
        private Label label;

        public KanbanTaskCard(KanbanBoard kanban, string textContent)
        {
            this.kanbanBoard = kanban;

            panel = new StackPanel
            {
                Orientation = Orientation.Horizontal
            };

            label = new Label
            {
                Content = textContent,
                Margin = new Thickness(5, 0, 5, 0),
                Padding = new Thickness(5),
                MinHeight = 50,
                MinWidth = 250,
                Background = Brushes.White,
                BorderBrush = Brushes.SkyBlue
            };

            label.MouseDown += (sender, e) => ItemMouseDown(sender, e);
            DragOver += (sender, e) => kanban.ItemPreviewShow(sender, e);
            Drop += (sender, e) => kanban.ItemDrop(sender, e);
            AllowDrop = true;

            panel.Children.Add(label);
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
            label.Padding = new Thickness(3);
            label.BorderThickness = new Thickness(2);
        }

        private void DisableHighlight()
        {
            label.Padding = new Thickness(5);
            label.BorderThickness = new Thickness(0);
        }
    }
}
