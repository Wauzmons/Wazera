using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media.Imaging;

namespace Wazera.Data
{
    public class PriorityData
    {
        public static PriorityData Critical { get; } = new PriorityData(1, "Critical", "prio_1.png");

        public static PriorityData High { get; } = new PriorityData(2, "High", "prio_2.png");

        public static PriorityData Normal { get; } = new PriorityData(3, "Normal", "prio_3.png");

        public static PriorityData Low { get; } = new PriorityData(4, "Low", "prio_4.png");

        public static PriorityData Insignificant { get; } = new PriorityData(5, "Insignificant", "prio_5.png");

        public int ID { get; }

        public string DisplayName { get; }

        private BitmapImage bitmapImage;

        private PriorityData(int id, string displayName, string iconPath)
        {
            ID = id;
            DisplayName = displayName;

            bitmapImage = new BitmapImage();
            bitmapImage.BeginInit();
            bitmapImage.UriSource = new Uri("pack://application:,,,/Resources/" + iconPath);
            bitmapImage.EndInit();
        }

        public Image GetImage()
        {
            return new Image()
            {
                Source = bitmapImage,
                Width = 12,
                Height = 12
            };
        }

        public StackPanel GetPanel()
        {
            StackPanel panel = new StackPanel
            {
                HorizontalAlignment = HorizontalAlignment.Left,
                Orientation = Orientation.Horizontal,
                Margin = new Thickness(5)
            };
            panel.Children.Add(GetImage());
            panel.Children.Add(new Label
            {
                HorizontalAlignment = HorizontalAlignment.Right,
                Content = DisplayName
            });
            return panel;
        }
    }
}
