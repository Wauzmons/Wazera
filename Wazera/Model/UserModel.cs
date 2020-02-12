using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Media.Imaging;
using Wazera.Data;
using WazeraSQL;

namespace Wazera.Model
{
    class UserModel : Entity<UserModel>
    {
        static UserModel()
        {
            TableName = "User";
            Columns = new EntityColumn[]
            {
                new EntityColumn("ID", EntityColumnType.Long, true, true),
                new EntityColumn("LoginName", EntityColumnType.String50, true),
                new EntityColumn("FirstName", EntityColumnType.String50, true),
                new EntityColumn("LastName", EntityColumnType.String50, true),
                new EntityColumn("Password", EntityColumnType.String256, true)
            };
            CreateTableIfNotExists();
        }

        public long ID { get; set; }

        public string LoginName { get; set; }

        public string FirstName { get; set; }

        public string LastName { get; set; }

        public string Password { get; set; }

        public BitmapImage Avatar { get; set; }

        public UserModel()
        {
            // Automated Initialization by WazeraSQL
        }

        public UserModel(UserData userData)
        {
            ID = userData.ID;
            LoginName = userData.LoginName;
            FirstName = userData.FirstName;
            LastName = userData.LastName;
            Password = Convert.ToBase64String(Encoding.UTF8.GetBytes(userData.Password));
            Avatar = WazeraUtils.GetResource("default_avatar.png");
        }

        public UserData ToData()
        {
            return new UserData(LoginName, FirstName, LastName, Avatar)
            {
                ID = ID,
                Password = Encoding.UTF8.GetString(Convert.FromBase64String(Password))
            };
        }

        public static UserData FindByUsernameAndPassword(string username, string password)
        {
            password = Convert.ToBase64String(Encoding.UTF8.GetBytes(password));
            List<UserModel> results = Find(new string[] { "LoginName = '" + username + "'", "Password = '" + password + "'"});
            return results.Count > 0 ? results[0].ToData() : null;
        }

        public static void DeleteById(long id)
        {
            Delete(new string[] { "ID = " + id });
        }

        public static void DeleteByStatusId(long id)
        {
            Delete(new string[] { "StatusID = " + id });
        }
    }
}
