using System.Text.RegularExpressions;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Shapes;
using Wazera.Model;
using Wazera.Project;

namespace Wazera.Kanban
{
    public class KanbanColumnOptions : StackPanel
    {
        public bool Editable { get; set; }

        public long ID { get; set; }

        private string Description { get; set; }

        private TextBox titleInput;
        private TextBox minCardsInput;
        private TextBox maxCardsInput;

        public KanbanColumnOptions(bool editable, string title, string description)
        {
            Editable = editable;
            Description = description;

            Orientation = Orientation.Vertical;
            Margin = new Thickness(10, 0, 10, 0);
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
                Margin = new Thickness(5),
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
                Margin = new Thickness(5),
                Padding = new Thickness(5)
            });
            minCardsInput = new TextBox
            {
                Text = "0",
                Width = 50,
                MaxLength = 4,
                Margin = new Thickness(5),
                Padding = new Thickness(5)
            };
            minCardsInput.PreviewTextInput += NumberValidation;
            minCardsInput.TextChanged += NumberValidation;
            limitPanel.Children.Add(minCardsInput);

            limitPanel.Children.Add(new Label
            {
                Content = "Max",
                Margin = new Thickness(5),
                Padding = new Thickness(5)
            });
            maxCardsInput = new TextBox
            {
                Text = "0",
                Width = 50,
                MaxLength = 4,
                Margin = new Thickness(5),
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
                    Margin = new Thickness(5),
                    Width = 25
                };
                removeButton.Click += (sender, e) => Remove();
                panel.Children.Add(removeButton);
            }
            panel.Children.Add(new Label
            {
                Content = description ?? "Custom status column",
                HorizontalAlignment = HorizontalAlignment.Left,
                Margin = new Thickness(5),
                Padding = new Thickness(5)
            });
            Children.Add(panel);

            Children.Add(new Rectangle
            {
                Height = 3,
                Fill = CreateProjectDialog.ReleaseDescription.Equals(Description) ? Brushes.LimeGreen : Brushes.Gold
            });
        }

        private void Remove()
        {
            if(ID > 0)
            {
                MessageBoxResult result = MessageBox.Show("Do you really want to delete \'" + titleInput.Text + "\' and ALL TASKS inside?", "Confirmation", MessageBoxButton.OKCancel);
                if (result == MessageBoxResult.OK)
                {
                    StatusModel.DeleteById(ID);
                    (Parent as StackPanel).Children.Remove(this);
                }
            }
            else
            {
                (Parent as StackPanel).Children.Remove(this);
            }
        }

        public string GetTitle()
        {
            return titleInput.Text;
        }

        public int GetMinCards()
        {
            return string.IsNullOrWhiteSpace(minCardsInput.Text) ? 1 : int.Parse(minCardsInput.Text);
        }

        public void SetMinCards(int minCards)
        {
            minCardsInput.Text = "" + minCards;
        }

        public int GetMaxCards()
        {
            return string.IsNullOrWhiteSpace(maxCardsInput.Text) ? 1 : int.Parse(maxCardsInput.Text);
        }

        public void SetMaxCards(int maxCards)
        {
            maxCardsInput.Text = "" + maxCards;
        }
    }
}
