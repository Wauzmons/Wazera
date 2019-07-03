using System;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using Wazera.Data;

namespace Wazera.Kanban
{
    class KanbanColumn : ListBox
    {
        private StatusData data;

        private KanbanBoard kanbanBoard;
        private Border border;
        private Label header;

        public KanbanColumn(KanbanBoard kanbanBoard, StatusData data)
        {
            this.data = data;
            this.kanbanBoard = kanbanBoard;

            HorizontalAlignment = HorizontalAlignment.Stretch;
            HorizontalContentAlignment = HorizontalAlignment.Stretch;
            VerticalAlignment = VerticalAlignment.Stretch;
            Margin = new Thickness(5);
            Background = Brushes.LightGray;
            BorderThickness = new Thickness(0);

            AddHeader();
            AddButton();

            border = new Border
            {
                Margin = new Thickness(5),
                Background = Brushes.LightGray,
                BorderBrush = Brushes.White,
                BorderThickness = new Thickness(3),
                CornerRadius = new CornerRadius(10)
            };
            border.Child = this;
        }

        public Border AsBorderedColumn()
        {
            return border;
        }

        public int GetCardCount()
        {
            return Items.Count - 2;
        }

        private void AddHeader()
        {
            header = new Label
            {
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
            int cardCount = GetCardCount();
            header.Content = data.Title.ToUpper() + " (" + cardCount + ")";
            if(data.HasCardMinimum() && cardCount < data.MinCards)
            {
                border.BorderBrush = Brushes.Blue;
            }
            else if(data.HasCardMaximum() && cardCount > data.MaxCards)
            {
                border.BorderBrush = Brushes.Red;
            }
            else
            {
                border.BorderBrush = Brushes.White;
            }
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
            KanbanTaskCard item = new KanbanTaskCard(kanbanBoard, textContent);
            Items.Insert(Items.Count - 1, item);
            UpdateHeader();
        }
    }
}
