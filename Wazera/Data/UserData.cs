using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace Wazera.Data
{
    public class UserData
    {
        public long ID { get; set; }

        public string LoginName { get; set; }

        public string FirstName { get; set; }

        public string LastName { get; set; }

        public BitmapImage Avatar { get; set; }

        public UserData(long id, string loginName, string firstName, string lastName, BitmapImage avatar)
        {
            ID = id;
            LoginName = loginName;
            FirstName = firstName;
            LastName = lastName;
            Avatar = avatar;
        }

        public string GetFullName()
        {
            return FirstName + " " + LastName;
        }

        public string GetShortName()
        {
            return (FirstName.Substring(0, 1) + LastName.Substring(0, 1)).ToUpper();
        }

        public StackPanel PanelShortName { get { return GetPanel(false); } }

        public StackPanel PanelFullName { get { return GetPanel(true); } }

        private StackPanel GetPanel(bool showFullName)
        {
            StackPanel panel = new StackPanel
            {
                HorizontalAlignment = HorizontalAlignment.Right,
                Orientation = Orientation.Horizontal,
                Margin = new Thickness(5),
                ToolTip = new Label
                {
                    Content = "Assigned User [" + GetFullName() + "]"
                }
            };
            panel.Children.Add(GetAvatarEllipse(18));
            panel.Children.Add(new Label
            {
                HorizontalAlignment = HorizontalAlignment.Right,
                Content = showFullName ? GetFullName() : GetShortName()
            });
            return panel;
        }

        public Ellipse GetAvatarEllipse(int diameter)
        {
            return new Ellipse
            {
                HorizontalAlignment = HorizontalAlignment.Right,
                Width = diameter,
                Height = diameter,
                Fill = new ImageBrush(Avatar),
                Stroke = Brushes.LightSlateGray,
                StrokeThickness = 1
            };
        }
    }
}
