using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Shapes;
using Wazera.Model;

namespace Wazera.Data
{
    public class CategoryData
    {
        public long ID { get; set; }

        public string Name { get; set; }

        public Color DisplayColor { get; set; }

        public CategoryData(string name, Color displayColor)
        {
            Name = name;
            DisplayColor = displayColor;
        }

        public static List<CategoryData> GetAllCategories()
        {
            List<CategoryData> categoryDatas = new List<CategoryData>();
            categoryDatas.Add(GetDefaultCategory());
            categoryDatas.AddRange(CategoryModel.FindAll());
            return categoryDatas;
        }

        public static CategoryData GetDefaultCategory() {
            return new CategoryData("No Category", Brushes.LightSlateGray.Color);
        }

        public StackPanel Panel { get { return GetPanel(); } }

        public StackPanel GetPanel()
        {
            StackPanel panel = new StackPanel
            {
                HorizontalAlignment = HorizontalAlignment.Left,
                Orientation = Orientation.Horizontal,
                Margin = new Thickness(5),
                ToolTip = new Label
                {
                    Content = "Category " + ID + " [" + Name + "]"
                }
            };
            panel.Children.Add(new Rectangle
            {
                Fill = new SolidColorBrush(DisplayColor),
                Width = 15,
                Height = 15
            });
            panel.Children.Add(new Label
            {
                HorizontalAlignment = HorizontalAlignment.Right,
                Content = Name
            });
            return panel;
        }
    }
}
