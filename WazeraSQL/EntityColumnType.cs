using System;

namespace WazeraSQL
{
    public class EntityColumnType
    {
        public static EntityColumnType Bool { get; } = new EntityColumnType(typeof(bool), "BIT");

        public static EntityColumnType Byte { get; } = new EntityColumnType(typeof(byte), "TINYINT");

        public static EntityColumnType Short { get; } = new EntityColumnType(typeof(short), "SMALLINT");

        public static EntityColumnType Int { get; } = new EntityColumnType(typeof(int), "INT");

        public static EntityColumnType Long { get; } = new EntityColumnType(typeof(long), "BIGINT");

        public static EntityColumnType Double { get; } = new EntityColumnType(typeof(double), "FLOAT");

        public static EntityColumnType String { get; } = new EntityColumnType(typeof(string), "TEXT");

        public static EntityColumnType String10 { get; } = new EntityColumnType(typeof(string), "VARCHAR(10)");

        public static EntityColumnType String50 { get; } = new EntityColumnType(typeof(string), "VARCHAR(50)");

        public static EntityColumnType String256 { get; } = new EntityColumnType(typeof(string), "VARCHAR(256)");

        public static EntityColumnType String1024 { get; } = new EntityColumnType(typeof(string), "VARCHAR(1024)");

        public static EntityColumnType String4096 { get; } = new EntityColumnType(typeof(string), "VARCHAR(4096)");

        public static EntityColumnType Text { get; } = new EntityColumnType(typeof(string), "TEXT");

        public static EntityColumnType Date { get; } = new EntityColumnType(typeof(DateTime), "DATE");

        public static EntityColumnType DateTime { get; } = new EntityColumnType(typeof(DateTime), "DATETIME");

        private Type csType;

        private string sqlType;

        private EntityColumnType(Type csType, string sqlType)
        {
            this.csType = csType;
            this.sqlType = sqlType;
        }

        public Type GetCsType()
        {
            return csType;
        }

        public string GetSqlType()
        {
            return sqlType;
        }
    }
}
