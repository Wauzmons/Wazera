using System;
using System.Windows.Media.Imaging;

namespace Wazera
{
    class UtilTool
    {
        public static BitmapImage GetResource(string path)
        {
            BitmapImage bitmapImage = new BitmapImage();
            bitmapImage.BeginInit();
            bitmapImage.UriSource = new Uri("pack://application:,,,/Resources/" + path);
            bitmapImage.EndInit();
            return bitmapImage;
        }
    }
}
