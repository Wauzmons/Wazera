using System;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;

namespace Wazera
{
    class KanbanColumn : ListBox
    {
        private Kanban kanban;

        private string title;
        private Label header;

        public KanbanColumn(Kanban kanban, string title)
        {
            this.kanban = kanban;
            this.title = title;

            HorizontalAlignment = HorizontalAlignment.Stretch;
            HorizontalContentAlignment = HorizontalAlignment.Stretch;
            VerticalAlignment = VerticalAlignment.Stretch;
            Margin = new Thickness(5);
            Background = Brushes.LightGray;
            BorderThickness = new Thickness(0);

            AddHeader(title);
            AddButton();
        }

        private void AddHeader(string title)
        {
            header = new Label
            {
                //HorizontalContentAlignment = HorizontalAlignment.Center,
                Margin = new Thickness(5),
                Padding = new Thickness(5),
                MinWidth = 250,
                FontWeight = FontWeights.Bold,
                Foreground = Brushes.DarkSlateGray,
                Background = Brushes.LightGray,
                Focusable = false
            };
            Items.Add(header);
        }

        public void UpdateHeader()
        {
            header.Content = title.ToUpper() + " (" + (Items.Count - 2) + ")";
        }

        private void AddButton()
        {
            Button button = new Button
            {
                Content = "New Task"
            };
            Items.Add(button);
        }

        public void AddTestRows()
        {
            Random random = new Random();
            string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

            for (int index = 0; index < 20; index++)
            {
                string randomText = new string(Enumerable.Repeat(chars, random.Next(16) + 3)
                    .Select(s => s[random.Next(s.Length)]).ToArray());

                AddRow(randomText);
            }
        }

        public void AddRow(string textContent)
        {
            KanbanTaskCard item = new KanbanTaskCard(kanban, textContent);
            Items.Insert(Items.Count - 1, item);
            UpdateHeader();
        }
    }
}
