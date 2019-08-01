using System;

namespace Wazera.Data.Database
{
    class EntityColumnType
    {
        public static EntityColumnType Int { get; } = new EntityColumnType(typeof(int), "INT");

        public static EntityColumnType Long { get; } = new EntityColumnType(typeof(long), "BIGINT");

        public static EntityColumnType String { get; } = new EntityColumnType(typeof(string), "TEXT");

        public static EntityColumnType String10 { get; } = new EntityColumnType(typeof(string), "VARCHAR(10)");

        public static EntityColumnType String50 { get; } = new EntityColumnType(typeof(string), "VARCHAR(50)");

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
