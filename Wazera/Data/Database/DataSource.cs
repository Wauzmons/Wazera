using MySql.Data.MySqlClient;
using System;
using System.IO;
using System.Text;
using System.Windows;
using Wazera.Data.Model;

namespace Wazera.Data.Database
{
    class DataSource
    {
        public static MySqlConnection Connection { get; set; }

        private static string connectionPath = AppDomain.CurrentDomain.BaseDirectory + "\\mysql.txt";

        //private static string ip = "";

        //private static string port = "";

        //private static string database = "";

        //private static string username = "";

        //private static string password = "";

        public static void Start()
        {
            try
            {
                //Example: SERVER=127.0.0.1;Port=3306;DATABASE=wazera_test;UID=wazera_admin;PASSWORD=********;
                string connectionString = File.ReadAllText(connectionPath, Encoding.UTF8);
                Connection = new MySqlConnection(connectionString);
                Connection.Open();
            }
            catch(MySqlException e)
            {
                switch(e.Number)
                {
                    case 0:
                        MessageBox.Show("Cannot connect to MySQL server, please contact your administrator!");
                        break;
                    case 1045:
                        MessageBox.Show("Invalid username/password, please try again!");
                        break;
                    default:
                        MessageBox.Show("Unexpected Error: " + e.ToString());
                        break;
                }
            }
        }
    }
}
