using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using Wazera.Data;
using Wazera.Model;
namespace Wazera.Project
{
    public class CategoryOptions : StackPanel
    {
        public CategoryData Data { get; set; }

        public CategoryOptions(CategoryData data)
        {
            Data = data;

            Orientation = Orientation.Vertical;
            Margin = new Thickness(10, 0, 10, 0);
            Background = Brushes.White;

            MouseEnter += (sender, e) => Background = new SolidColorBrush(Color.FromArgb(255, 190, 230, 253));
            MouseLeave += (sender, e) => Background = Brushes.White;

            Grid grid = new Grid
            {
                IsEnabled = Data.ID > 0
            };
            Border border = new Border
            {
                Child = grid,
                BorderBrush = Brushes.MediumPurple,
                BorderThickness = new Thickness(0, 0, 0, 3)
            };
            AddColorPicker(grid, Data.DisplayColor);
            AddTitle(grid, Data.Name + " (ID: " + Data.ID + ")");
            AddRemoveButton(grid);

            Children.Add(border);
        }

        private void AddColorPicker(Grid grid, Color color)
        {
            Xceed.Wpf.Toolkit.ColorPicker colorPicker = new Xceed.Wpf.Toolkit.ColorPicker
            {
                SelectedColor = color,
                HorizontalAlignment = HorizontalAlignment.Left,
                VerticalAlignment = VerticalAlignment.Center,
                Width = 50,
                Margin = new Thickness(10, 5, 5, 5),
                Padding = new Thickness(5),
            };
            colorPicker.SelectedColorChanged += (sender, e) => UpdateColor(colorPicker);
            grid.Children.Add(colorPicker);
        }

        private void AddTitle(Grid grid, string title)
        {
            grid.Children.Add(new Label
            {
                Content = title,
                HorizontalAlignment = HorizontalAlignment.Left,
                VerticalAlignment = VerticalAlignment.Center,
                Margin = new Thickness(60, 10, 5, 10),
                Padding = new Thickness(5),
                FontSize = 16
            });
        }

        private void AddRemoveButton(Grid grid)
        {
            Button removeButton = new Button
            {
                Content = "X",
                HorizontalAlignment = HorizontalAlignment.Right,
                VerticalAlignment = VerticalAlignment.Center,
                Margin = new Thickness(5),
                Width = 25,
                Height = 25
            };
            removeButton.Click += (sender, e) => Remove();
            grid.Children.Add(removeButton);
        }

        private void UpdateColor(Xceed.Wpf.Toolkit.ColorPicker colorPicker)
        {
            Data.DisplayColor = (Color) colorPicker.SelectedColor;
            new CategoryModel(Data).Save();
        }

        private void Remove()
        {
            MessageBoxResult result = MessageBox.Show("Do you really want to delete \'" + Data.Name + "\'?", "Confirmation", MessageBoxButton.OKCancel);
            if (result == MessageBoxResult.OK)
            {
                CategoryModel.DeleteById(Data.ID);
                (Parent as StackPanel).Children.Remove(this);
            }
        }
    }
}
