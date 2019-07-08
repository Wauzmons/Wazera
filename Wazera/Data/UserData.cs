using System.Windows.Media.Imaging;

namespace Wazera.Data
{
    class UserData
    {
        public static UserData GetMockUser()
        {
            return new UserData(0, "Test", "Peter", "Penguin", UtilTool.GetResource("default_avatar.png"));
        }

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
    }
}
