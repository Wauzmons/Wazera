namespace WazeraSQL
{
    public class EntityColumn
    {
        public string PropertyName { get; set; }

        public EntityColumnType ColumnType { get; set; }

        public bool NotNull { get; set; }

        public bool AutoIncrement { get; set; }

        public EntityColumn(string propertyName, EntityColumnType columnType)
        {
            PropertyName = propertyName;
            ColumnType = columnType;
        }

        public EntityColumn(string propertyName, EntityColumnType columnType, bool notNull)
        {
            PropertyName = propertyName;
            ColumnType = columnType;
            NotNull = notNull;
        }

        public EntityColumn(string propertyName, EntityColumnType columnType, bool notNull, bool autoIncrement)
        {
            PropertyName = propertyName;
            ColumnType = columnType;
            NotNull = notNull;
            AutoIncrement = autoIncrement;
        }

        public string GetQueryString()
        {
            return
                PropertyName + " "
                + ColumnType.GetSqlType()
                + (NotNull ? " NOT NULL" : "")
                + (AutoIncrement ? " AUTO_INCREMENT" : "");
        }
    }
}
