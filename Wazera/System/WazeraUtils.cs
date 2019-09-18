using System;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace Wazera
{
    class WazeraUtils
    {
        public static Image GetImage(string path, int diameter)
        {
            return new Image
            {
                Source = GetResource(path),
                Width = diameter,
                Height = diameter
            };
        }

        public static BitmapImage GetResource(string path)
        {
            BitmapImage bitmapImage = new BitmapImage();
            bitmapImage.BeginInit();
            bitmapImage.UriSource = new Uri("pack://application:,,,/Resources/" + path);
            bitmapImage.EndInit();
            return bitmapImage;
        }

        public static string ColorToHex(Color color)
        {
            return "#" + color.R.ToString("X2") + color.G.ToString("X2") + color.B.ToString("X2");
        }

        public static Color HexToColor(string color)
        {
            return (Color) ColorConverter.ConvertFromString(color);
        }
    }
}
