using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using Wazera.Data;
using Wazera.Project;

namespace Wazera.Kanban
{
    public partial class KanbanBoard : Window
    {
        public ProjectData Data { get; set; }
        public ProjectView View { get; set; }

        public bool IsBacklog { get; set; }
        public int ColumnCount { get; set; } = 0;

        public Window DragDropWindow { get; set; }
        public Label DragDropPreviewShadow { get; set; }

        public KanbanBoard(ProjectData data, ProjectView view, bool isBacklog)
        {
            Data = data;
            View = view;
            IsBacklog = isBacklog;

            InitializeComponent();
            columns.DataContext = this;

            if(IsBacklog)
            {
                Data.Backlog.Tasks = Data.Backlog.Tasks
                    .OrderBy(task => task.Priority.ID)
                    .ThenBy(task => task.ID)
                    .ToList();
                AddColumn(Data.Backlog);
                return;
            }
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

        public void ItemDrop(object sender, DragEventArgs e)
        {
            DragDropWindowRemove();
            if (DragDropPreviewShadow == null)
            {
                return;
            }

            KanbanColumn newColumn = DragDropPreviewShadow.Parent as KanbanColumn;
            int index = newColumn.Items.IndexOf(DragDropPreviewShadow);
            ItemPreviewRemove();
            if (e.Data.GetData(typeof(KanbanTaskCard)) is KanbanTaskCard cardItem && !cardItem.Equals(DragDropPreviewShadow))
            {
                MoveTaskCard(newColumn, cardItem, index);
            }
        }

        public void DragDropWindowRemove()
        {
            if (DragDropWindow != null)
            {
                DragDropWindow.Close();
                DragDropWindow = null;
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
            this.DragDropPreviewShadow = itemPreviewShadow;
        }

        public void ItemPreviewShow(KanbanColumn column)
        {
            if (this.DragDropPreviewShadow != null && this.DragDropPreviewShadow.Parent.Equals(column))
            {
                return;
            }
            Label itemPreviewShadow = GetItemPreviewShadow();
            column.Items.Insert(column.Items.Count, itemPreviewShadow);
            this.DragDropPreviewShadow = itemPreviewShadow;
        }

        public void ItemPreviewRemove()
        {
            if (DragDropPreviewShadow == null)
            {
                return;
            }
            KanbanColumn column = DragDropPreviewShadow.Parent as KanbanColumn;
            if (column == null)
            {
                return;
            }
            column.Items.Remove(DragDropPreviewShadow);
            DragDropPreviewShadow = null;
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
            column.AddRow(item.Data, index);
            item = null;

            oldColumn.UpdateHeader();
            column.UpdateHeader();

            oldColumn.SaveData();
            column.SaveData();
        }
    }
}
