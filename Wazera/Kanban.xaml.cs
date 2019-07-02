using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;

namespace Wazera
{
    public partial class Kanban : Window
    {
        public Kanban()
        {
            InitializeComponent();
            AddColumn("Planned");
            AddColumn("In Progress");
            AddColumn("Done");
        }

        public void AddColumn(string title)
        {
            KanbanColumn column = new KanbanColumn(this, title);
            column.AddTestRows();

            Border border = new Border
            {
                Margin = new Thickness(8),
                Background = Brushes.LightGray,
                BorderBrush = Brushes.LightGray,
                BorderThickness = new Thickness(0),
                CornerRadius = new CornerRadius(10)
            };
            border.Child = column;
            columns.Items.Add(border);
        }
    }
}
