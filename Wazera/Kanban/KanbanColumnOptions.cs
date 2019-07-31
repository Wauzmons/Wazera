using System.Text.RegularExpressions;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Shapes;

namespace Wazera.Kanban
{
    public class KanbanColumnOptions : StackPanel
    {
        public bool Editable { get; set; }

        private TextBox titleInput;
        private TextBox minCardsInput;
        private TextBox maxCardsInput;

        public KanbanColumnOptions(bool editable, string title, string description)
        {
            Editable = editable;

            Orientation = Orientation.Vertical;
            Margin = new Thickness(6, editable ? 0 : 6, 6, 6);
            Background = Brushes.White;

            MouseEnter += (sender, e) => Background = new SolidColorBrush(Color.FromArgb(255, 190, 230, 253));
            MouseLeave += (sender, e) => Background = Brushes.White;

            Grid grid = new Grid
            {
                IsEnabled = Editable
            };
            AddTitleInput(grid, title);
            AddLimitPanel(grid);
            Children.Add(grid);

            AddDescription(description);
        }

        public void AddTitleInput(Grid grid, string defaultTitle)
        {
            titleInput = new TextBox
            {
                Text = defaultTitle,
                HorizontalAlignment = HorizontalAlignment.Left,
                Width = 360,
                MaxLength = 50,
                Padding = new Thickness(5)
            };
            grid.Children.Add(titleInput);
        }

        private void AddLimitPanel(Grid grid)
        {
            StackPanel limitPanel = new StackPanel
            {
                Orientation = Orientation.Horizontal,
                HorizontalAlignment = HorizontalAlignment.Right,
                ToolTip = new Label
                {
                    Content = "If the column's task count gets out of range it will be highlighted"
                }
            };

            limitPanel.Children.Add(new Label
            {
                Content = "Min",
                Padding = new Thickness(5)
            });
            minCardsInput = new TextBox
            {
                Text = "0",
                Width = 50,
                MaxLength = 4,
                Padding = new Thickness(5)
            };
            minCardsInput.PreviewTextInput += NumberValidation;
            minCardsInput.TextChanged += NumberValidation;
            limitPanel.Children.Add(minCardsInput);

            limitPanel.Children.Add(new Label
            {
                Content = "Max",
                Padding = new Thickness(5)
            });
            maxCardsInput = new TextBox
            {
                Text = "0",
                Width = 50,
                MaxLength = 4,
                Padding = new Thickness(5)
            };
            maxCardsInput.PreviewTextInput += NumberValidation;
            maxCardsInput.TextChanged += NumberValidation;
            limitPanel.Children.Add(maxCardsInput);

            grid.Children.Add(limitPanel);
        }

        private void NumberValidation(object sender, TextCompositionEventArgs e)
        {
            e.Handled = !new Regex("^[0-9]*$").IsMatch(e.Text);
        }

        private void NumberValidation(object sender, TextChangedEventArgs e)
        {
            if(!new Regex("^[0-9]*$").IsMatch((sender as TextBox).Text))
            {
                (sender as TextBox).Text = "";
            }
        }

        private void AddDescription(string description)
        {
            StackPanel panel = new StackPanel
            {
                Orientation = Orientation.Horizontal
            };
            if(description == null)
            {
                Button removeButton = new Button
                {
                    Content = "X",
                    HorizontalAlignment = HorizontalAlignment.Left,
                    Width = 25
                };
                removeButton.Click += (sender, e) => (Parent as StackPanel).Children.Remove(this);
                panel.Children.Add(removeButton);
            }
            panel.Children.Add(new Label
            {
                Content = description ?? "Custom status column",
                HorizontalAlignment = HorizontalAlignment.Left,
                Padding = new Thickness(5)
            });
            Children.Add(panel);

            Children.Add(new Rectangle
            {
                Height = 3,
                Fill = Brushes.Gold
            });
        }

        public string GetTitle()
        {
            return titleInput.Text;
        }

        public int GetMinCards()
        {
            return string.IsNullOrWhiteSpace(minCardsInput.Text) ? 1 : int.Parse(minCardsInput.Text);
        }

        public int GetMaxCards()
        {
            return string.IsNullOrWhiteSpace(maxCardsInput.Text) ? 1 : int.Parse(maxCardsInput.Text);
        }
    }
}
