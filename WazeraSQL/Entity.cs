using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Reflection;

namespace WazeraSQL
{
    public class Entity<T>
    {
        private static Dictionary<Type, string> tableNames = new Dictionary<Type, string>();
        protected static string TableName { get => tableNames[typeof(T)]; set => tableNames.Add(typeof(T), value); }

        private static Dictionary<Type, EntityColumn[]> tableColumns = new Dictionary<Type, EntityColumn[]>();
        protected static EntityColumn[] Columns { get => tableColumns[typeof(T)]; set => tableColumns.Add(typeof(T), value); }

        public static void CreateTableIfNotExists()
        {
            bool tableExists = false;

            MySqlCommand command = new MySqlCommand("SELECT Count(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '" + TableName + "'", DataSource.Connection);
            using (MySqlDataReader reader = command.ExecuteReader())
            {
                reader.Read();
                tableExists = int.Parse(reader[0].ToString()) > 0;
            }
            if (!tableExists)
            {
                Console.WriteLine("Creating Table: " + TableName);
                Console.WriteLine("Created Table: " + TableName + " (" + CreateTable() + " rows affected)");
            }
            else
            {
                Console.WriteLine("Loaded Table: " + TableName);
            }
        }

        private static int CreateTable()
        {
            string commandString = "CREATE TABLE " + TableName + "(";
            foreach(EntityColumn column in Columns)
            {
                commandString += column.GetQueryString() + ", ";
            }
            commandString += "PRIMARY KEY (" + Columns[0].PropertyName + "))";

            MySqlCommand command = new MySqlCommand(commandString, DataSource.Connection);
            return command.ExecuteNonQuery();
        }

        public long Save()
        {
            bool isTransient = IsTransient();
            string commandString;
            if(isTransient)
            {
                commandString = "INSERT INTO " + TableName + "(";
                foreach (EntityColumn column in Columns)
                {
                    if (column.AutoIncrement)
                    {
                        continue;
                    }
                    commandString += column.PropertyName;
                    commandString += (Columns[Columns.Length - 1].Equals(column) ? "" : ", ");
                }

                commandString += ") VALUES (";
                foreach (EntityColumn column in Columns)
                {
                    if (column.AutoIncrement)
                    {
                        continue;
                    }
                    commandString += AsSqlParameter(column);
                    commandString += (Columns[Columns.Length - 1].Equals(column) ? "" : ", ");
                }
                commandString += ")";
            }
            else
            {
                commandString = "UPDATE " + TableName + " SET ";
                foreach (EntityColumn column in Columns)
                {
                    if (column.AutoIncrement)
                    {
                        continue;
                    }
                    commandString += column.PropertyName + " = " + AsSqlParameter(column);
                    commandString += (Columns[Columns.Length - 1].Equals(column) ? "" : ", ");
                }
                commandString += " WHERE " + Columns[0].PropertyName + " = " + AsSqlParameter(Columns[0]);
            }

            MySqlCommand command = new MySqlCommand(commandString, DataSource.Connection);
            command.ExecuteNonQuery();

            return isTransient ? GetLastInsertId() : (long) GetType().GetProperty(Columns[0].PropertyName).GetValue(this);
        }

        public bool IsTransient()
        {
            object value = GetType().GetProperty(Columns[0].PropertyName).GetValue(this);
            return value == null || value.ToString().Equals("0");
        }

        public static long GetLastInsertId()
        {
            MySqlCommand command = new MySqlCommand("SELECT LAST_INSERT_ID()", DataSource.Connection);
            using (MySqlDataReader reader = command.ExecuteReader())
            {
                reader.Read();
                return long.Parse(reader[0].ToString());
            }
        }

        private string AsSqlParameter(EntityColumn column)
        {
            object value = GetType().GetProperty(column.PropertyName).GetValue(this);
            string result;

            if (value == null)
                result = "NULL";
            else if (value is string)
                result = "'" + ("" + value).Replace("'", "\"") + "'";
            else
                result = ("" + value).Replace("'", "\"");

            return result;
        }

        public static List<T> Find(string[] conditions)
        {
            List<T> results = new List<T>();

            string commandString = "SELECT * FROM " + TableName;
            foreach (string condition in conditions)
            {
                bool firstCondition = conditions[0].Equals(condition);
                commandString += firstCondition ? " WHERE " + condition : " AND " + condition;
            }

            MySqlCommand command = new MySqlCommand(commandString, DataSource.Connection);
            using (MySqlDataReader reader = command.ExecuteReader())
            {
                while (reader.Read())
                {
                    T entity = (T) Activator.CreateInstance(typeof(T));
                    for (int index = 0; index < Columns.Length; index++)
                    {
                        EntityColumn column = Columns[index];
                        PropertyInfo property = entity.GetType().GetProperty(column.PropertyName);
                        Type type = column.ColumnType.GetCsType();
                        var value = reader[index];
                        if(value is DBNull)
                        {
                            continue;
                        }
                        property.SetValue(entity, Convert.ChangeType(value, type));
                    }
                    results.Add(entity);
                }
            }

            return results;
        }

        public static int Delete(string[] conditions)
        {
            string commandString = "DELETE FROM " + TableName;
            foreach (string condition in conditions)
            {
                bool firstCondition = conditions[0].Equals(condition);
                commandString += firstCondition ? " WHERE " + condition : " AND " + condition;
            }

            MySqlCommand command = new MySqlCommand(commandString, DataSource.Connection);
            return command.ExecuteNonQuery();
        }
    }
}
