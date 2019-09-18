using System.Collections.Generic;
using Wazera.Data;
using WazeraSQL;

namespace Wazera.Model
{
    class CategoryModel : Entity<CategoryModel>
    {
        static CategoryModel()
        {
            TableName = "Category";
            Columns = new EntityColumn[]
            {
                new EntityColumn("ID", EntityColumnType.Long, true, true),
                new EntityColumn("Name", EntityColumnType.String50, true),
                new EntityColumn("DisplayColor", EntityColumnType.String10, true),
            };
            CreateTableIfNotExists();
        }

        public long ID { get; set; }

        public string Name { get; set; }

        public string DisplayColor { get; set; }

        public CategoryModel()
        {

        }

        public CategoryModel(CategoryData categoryData)
        {
            ID = categoryData.ID;
            Name = categoryData.Name;
            DisplayColor = WazeraUtils.ColorToHex(categoryData.DisplayColor);
        }

        public CategoryData ToData()
        {
            return new CategoryData(Name, WazeraUtils.HexToColor(DisplayColor))
            {
                ID = ID
            };
        }

        public static List<CategoryData> FindAll()
        {
            List<CategoryData> results = new List<CategoryData>();
            foreach (CategoryModel categoryModel in Find(new string[] { }))
            {
                results.Add(categoryModel.ToData());
            }
            return results;
        }

        public static CategoryData FindById(long id)
        {
            List<CategoryModel> results = Find(new string[] { "ID = " + id });
            return results.Count > 0 ? results[0].ToData() : null;
        }

        public static void DeleteById(long id)
        {
            Delete(new string[] { "ID = " + id });
        }
    }
}
