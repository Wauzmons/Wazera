using MySql.Data.MySqlClient;
using System;
using System.IO;
using System.Text;

namespace WazeraSQL
{
    public class DataSource
    {
        public static MySqlConnection Connection { get; set; }

        private static readonly string connectionPath = AppDomain.CurrentDomain.BaseDirectory + "\\mysql.txt";

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
                        Console.WriteLine("Cannot connect to MySQL server, please contact your administrator!");
                        break;
                    case 1045:
                        Console.WriteLine("Invalid username/password, please try again!");
                        break;
                    default:
                        Console.WriteLine("Unexpected Error: " + e.ToString());
                        break;
                }
            }
        }
    }
}
