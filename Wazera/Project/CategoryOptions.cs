using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using Wazera.Data;
using Wazera.Model;
using Xceed.Wpf.Toolkit;

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
            AddColorPicker(grid, Data.DisplayColor);
            AddTitle(grid, Data.Name + " (ID: " + Data.ID + ")");

            Children.Add(grid);
        }

        private void AddColorPicker(Grid grid, Color color)
        {
            ColorPicker colorPicker = new ColorPicker
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
                Margin = new Thickness(60, 5, 5, 5),
                Padding = new Thickness(5),
                FontSize = 16
            });
        }

        private void UpdateColor(ColorPicker colorPicker)
        {
            Data.DisplayColor = (Color) colorPicker.SelectedColor;
            new CategoryModel(Data).Save();
        }
    }
}
