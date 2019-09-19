using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media.Imaging;

namespace Wazera.Data
{
    public class PriorityData
    {
        static PriorityData() {
            Priorities = new List<PriorityData>();
            PriorityMap = new Dictionary<long, PriorityData>();

            Critical = new PriorityData(1, "Critical", "prio_1.png");
            High = new PriorityData(2, "High", "prio_2.png");
            Normal = new PriorityData(3, "Normal", "prio_3.png");
            Low = new PriorityData(4, "Low", "prio_4.png");
            Insignificant = new PriorityData(5, "Insignificant", "prio_5.png");
        }

        public static PriorityData Critical { get; }

        public static PriorityData High { get; }

        public static PriorityData Normal { get; }

        public static PriorityData Low { get; }

        public static PriorityData Insignificant { get; }

        public static List<PriorityData> Priorities { get; }

        public static Dictionary<long, PriorityData> PriorityMap { get; }

        public long ID { get; }

        public string DisplayName { get; }

        private BitmapImage bitmapImage;

        private PriorityData(long id, string displayName, string iconName)
        {
            ID = id;
            DisplayName = displayName;
            bitmapImage = WazeraUtils.GetResource(iconName);
            Priorities.Add(this);
            PriorityMap.Add(ID, this);
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
                    Content = "Priority " + ID + " [" + DisplayName + "]"
                }
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
