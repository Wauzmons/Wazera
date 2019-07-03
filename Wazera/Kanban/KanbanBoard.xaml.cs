using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using Wazera.Data;

namespace Wazera.Kanban
{
    public partial class KanbanBoard : Window
    {
        public KanbanBoard()
        {
            InitializeComponent();

            AddColumn(new StatusData("Planned"));
            AddColumn(new StatusData("In Progress", 10, 25));
            AddColumn(new StatusData("Done"));
        }

        public void AddColumn(StatusData statusData)
        {
            KanbanColumn column = new KanbanColumn(this, statusData);
            column.AddTestRows();
            columns.Items.Add(column.AsBorderedColumn());
        }

        #region Drag and Drop

        private Label itemPreviewShadow;

        public void ItemDrop(object sender, DragEventArgs e)
        {
            KanbanTaskCard item = e.Data.GetData(typeof(KanbanTaskCard)) as KanbanTaskCard;
            FrameworkElement target = sender as FrameworkElement;
            if (item == null || target == null)
            {
                return;
            }
            if (item.Equals(target))
            {
                return;
            }

            Point dropPosition = e.GetPosition(sender as IInputElement);
            bool showBelow = target.ActualHeight / 2 < dropPosition.Y;

            KanbanColumn oldColumn = item.Parent as KanbanColumn;
            KanbanColumn newColumn = target.Parent as KanbanColumn;
            int targetIndex = newColumn.Items.IndexOf(target);

            ItemPreviewRemove();
            oldColumn.Items.Remove(item);
            newColumn.Items.Insert(showBelow ? targetIndex + 1 : targetIndex, item);
            oldColumn.UpdateHeader();
            newColumn.UpdateHeader();
        }

        public void ItemPreviewShow(object sender, DragEventArgs e)
        {
            KanbanTaskCard target = sender as KanbanTaskCard;
            Point dropPosition = e.GetPosition(sender as IInputElement);
            bool showBelow = target.ActualHeight / 2 < dropPosition.Y;

            ItemPreviewRemove();
            Label itemPreviewShadow = new Label
            {
                Content = "",
                Margin = new Thickness(5, 0, 5, 0),
                Padding = new Thickness(5),
                MinHeight = 50,
                MinWidth = 250,
                Background = Brushes.DarkGray,
                AllowDrop = true
            };
            itemPreviewShadow.Drop += (sender2, e2) => ItemDrop(sender2, e2);

            KanbanColumn newColumn = target.Parent as KanbanColumn;
            int targetIndex = newColumn.Items.IndexOf(target);
            newColumn.Items.Insert(showBelow ? targetIndex + 1 : targetIndex, itemPreviewShadow);
            this.itemPreviewShadow = itemPreviewShadow;
        }

        public void ItemPreviewRemove()
        {
            if(itemPreviewShadow == null)
            {
                return;
            }
            KanbanColumn column = itemPreviewShadow.Parent as KanbanColumn;
            if(column == null)
            {
                return;
            }
            column.Items.Remove(itemPreviewShadow);
            itemPreviewShadow = null;
        }

        #endregion
    }
}
