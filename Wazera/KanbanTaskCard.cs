using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;

namespace Wazera
{
    class KanbanTaskCard : ListViewItem
    {
        private Kanban kanban;

        private StackPanel panel;

        public KanbanTaskCard(Kanban kanban, string textContent)
        {
            this.kanban = kanban;

            Label label = new Label
            {
                Content = textContent,
                Margin = new Thickness(5, 0, 5, 0),
                Padding = new Thickness(5),
                MinHeight = 50,
                MinWidth = 250,
                Background = Brushes.White
            };

            panel = new StackPanel
            {
                Orientation = Orientation.Horizontal
            };

            label.MouseDown += (sender, e) => ItemMouseDown(sender, e, this);
            //DragEnter += (sender, e) => ItemPreview(sender, e);
            Drop += (sender, e) => ItemDrop(sender, e);
            AllowDrop = true;

            

            panel.Children.Add(label);
            Content = panel;
        }

        public KanbanColumn GetParent()
        {
            return Parent as KanbanColumn;
        }

        #region DragAndDrop

        private void ItemMouseDown(object sender, MouseEventArgs e, KanbanTaskCard item)
        {
            DragDrop.DoDragDrop(item, item, DragDropEffects.All);
        }

        private void ItemDrop(object sender, DragEventArgs e)
        {
            KanbanTaskCard item = e.Data.GetData(typeof(KanbanTaskCard)) as KanbanTaskCard;
            KanbanTaskCard target = sender as KanbanTaskCard;
            if(item.Equals(target))
            {
                return;
            }

            KanbanColumn oldColumn = item.GetParent();
            KanbanColumn newColumn = target.GetParent();
            int targetIndex = newColumn.Items.IndexOf(target);

            oldColumn.Items.Remove(item);
            newColumn.Items.Insert(targetIndex, item);
            oldColumn.UpdateHeader();
            newColumn.UpdateHeader();
        }

        #endregion

        #region Animation

        private void ItemPreview(object sender, DragEventArgs e)
        {
            Label shadow = new Label
            {
                Content = "",
                Margin = new Thickness(5, 0, 5, 0),
                Padding = new Thickness(5),
                MinHeight = 50,
                MinWidth = 250,
                Background = Brushes.DarkGray
            };
            shadow.MouseLeave += (sender2, e2) => ItemPreviewRemove(shadow);

            KanbanTaskCard target = sender as KanbanTaskCard;

            KanbanColumn newColumn = target.GetParent();
            int targetIndex = newColumn.Items.IndexOf(target);

            newColumn.Items.Insert(targetIndex, shadow);
        }

        private void ItemPreviewRemove(Label shadow)
        {
            KanbanColumn column = shadow.Parent as KanbanColumn;
            column.Items.Remove(shadow);
        }

        #endregion
    }
}
