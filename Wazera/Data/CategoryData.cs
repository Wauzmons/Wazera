using System.Collections.Generic;
using System.Windows.Media;
using Wazera.Model;

namespace Wazera.Data
{
    public class CategoryData
    {
        public long ID { get; set; }

        public string Name { get; set; }

        public Color DisplayColor { get; set; }

        public CategoryData(string name, Color displayColor)
        {
            Name = name;
            DisplayColor = displayColor;
        }

        public static List<CategoryData> GetAllCategories()
        {
            List<CategoryData> categoryDatas = new List<CategoryData>();
            categoryDatas.Add(new CategoryData("No Category", Brushes.LightSlateGray.Color));
            categoryDatas.AddRange(CategoryModel.FindAll());
            return categoryDatas;
        }
    }
}
