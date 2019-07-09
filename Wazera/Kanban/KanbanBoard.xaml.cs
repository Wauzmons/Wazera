using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using Wazera.Data;

namespace Wazera.Kanban
{
    public partial class KanbanBoard : Window
    {
        public ProjectData Data { get; set; }

        public int ColumnCount { get; set; } = 0;

        public KanbanBoard(ProjectData data)
        {
            Data = data;

            InitializeComponent();
            columns.DataContext = this;
            foreach(StatusData status in data.Statuses)
            {
                AddColumn(status);
            }
        }

        public KanbanColumn AddColumn(StatusData status)
        {
            ColumnCount++;
            KanbanColumn column = new KanbanColumn(this, status);
            foreach(TaskData task in status.Tasks)
            {
                column.AddRow(task);
            }
            columns.Items.Add(column.AsBorderedColumn());
            return column;
        }

        #region Drag and Drop

        private Label itemPreviewShadow;

        public void ItemDrop(object sender, DragEventArgs e)
        {
            if (itemPreviewShadow == null)
            {
                return;
            }
            KanbanColumn newColumn = itemPreviewShadow.Parent as KanbanColumn;
            int index = newColumn.Items.IndexOf(itemPreviewShadow);

            if (e.Data.GetData(typeof(string)) is string stringItem)
            {
                //ItemPreviewRemove();
                //newColumn.AddRow(new TaskData(stringItem), index);
            }
            else if (e.Data.GetData(typeof(KanbanTaskCard)) is KanbanTaskCard cardItem && !cardItem.Equals(itemPreviewShadow))
            {
                ItemPreviewRemove();
                MoveTaskCard(newColumn, cardItem, index);
            }
        }

        public void ItemPreviewShow(object sender, DragEventArgs e)
        {
            KanbanTaskCard target = sender as KanbanTaskCard;
            Point dropPosition = e.GetPosition(sender as IInputElement);
            bool showBelow = target.ActualHeight / 2 < dropPosition.Y;

            Label itemPreviewShadow = GetItemPreviewShadow();
            KanbanColumn newColumn = target.Parent as KanbanColumn;
            int targetIndex = newColumn.Items.IndexOf(target);
            newColumn.Items.Insert(showBelow ? targetIndex + 1 : targetIndex, itemPreviewShadow);
            this.itemPreviewShadow = itemPreviewShadow;
        }

        public void ItemPreviewShow(KanbanColumn column)
        {
            if (this.itemPreviewShadow != null && this.itemPreviewShadow.Parent.Equals(column))
            {
                return;
            }
            Label itemPreviewShadow = GetItemPreviewShadow();
            column.Items.Insert(column.Items.Count, itemPreviewShadow);
            this.itemPreviewShadow = itemPreviewShadow;
        }

        public void ItemPreviewRemove()
        {
            if (itemPreviewShadow == null)
            {
                return;
            }
            KanbanColumn column = itemPreviewShadow.Parent as KanbanColumn;
            if (column == null)
            {
                return;
            }
            column.Items.Remove(itemPreviewShadow);
            itemPreviewShadow = null;
        }

        private Label GetItemPreviewShadow()
        {
            ItemPreviewRemove();
            Label itemPreviewShadow = new Label
            {
                Content = "",
                Margin = new Thickness(3),
                Padding = new Thickness(5),
                MinHeight = 50,
                MinWidth = 180,
                Background = Brushes.DarkGray,
                AllowDrop = true
            };
            itemPreviewShadow.Drop += (sender2, e2) => ItemDrop(sender2, e2);
            return itemPreviewShadow;
        }

        #endregion

        public void MoveTaskCard(KanbanColumn column, KanbanTaskCard item, int index)
        {
            KanbanColumn oldColumn = item.Parent as KanbanColumn;
            if (column.Equals(oldColumn) && column.Items.IndexOf(item) < index)
            {
                index--;
            }

            oldColumn.Items.Remove(item);
            column.Items.Insert(index, item);

            oldColumn.UpdateHeader();
            column.UpdateHeader();

            oldColumn.SaveData();
            column.SaveData();
        }
    }
}
