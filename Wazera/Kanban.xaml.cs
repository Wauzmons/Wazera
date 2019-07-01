using System;
using System.Linq;
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
            AddColumn("Column A");
            AddColumn("Column B");
            AddColumn("Column C");
            AddColumn("Column D");
            AddColumn("Column E");
            AddColumn("Column F");
            AddColumn("Column G");
        }

        public void AddColumn(string content)
        {
            ListBox column = new ListBox
            {
                HorizontalAlignment = HorizontalAlignment.Stretch,
                HorizontalContentAlignment = HorizontalAlignment.Stretch,
                VerticalAlignment = VerticalAlignment.Stretch,
                Margin = new Thickness(5),
                BorderThickness = new Thickness(3),
                BorderBrush = Brushes.Black,
                Background = Brushes.LightBlue
            };
            Label header = new Label
            {
                Content = content,
                HorizontalContentAlignment = HorizontalAlignment.Center,
                FontSize = 16,
                FontWeight = FontWeights.Bold,
                Background = Brushes.Azure
            };
            column.Items.Add(header);
            AddTestRows(column);

            Button button = new Button
            {
                Content = "New Task"
            };
            column.Items.Add(button);
            columns.Items.Add(column);
        }

        public void AddTestRows(ListBox column)
        {
            for (int index = 0; index < 20; index++)
                AddRow(column, GetRandomString());
        }

        public void AddRow(ListBox column, string content)
        {
            Label label = new Label
            {
                Content = content,
                Background = Brushes.FloralWhite
            };
            ListBoxItem item = new ListBoxItem
            {
                
            };
            column.Items.Add(label);
        }

        private static Random random = new Random();
        private static string GetRandomString()
        {
            const string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            return new string(Enumerable.Repeat(chars, random.Next(16) + 3)
                .Select(s => s[random.Next(s.Length)]).ToArray());
        }
    }
}
