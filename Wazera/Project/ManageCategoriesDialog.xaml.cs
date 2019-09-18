using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using Wazera.Data;
using Wazera.Model;

namespace Wazera.Project
{
    public partial class ManageCategoriesDialog : Window
    {
        public ManageCategoriesDialog()
        {
            InitializeComponent();

            foreach(CategoryData categoryData in CategoryData.GetAllCategories())
            {
                categoryList.Children.Add(new CategoryOptions(categoryData));
            }
        }

        private void AddButton_Click(object sender, RoutedEventArgs e)
        {
            string categoryName = string.IsNullOrWhiteSpace(nameInput.Text) ? "Unnamed Category" : nameInput.Text;
            CategoryData categoryData = new CategoryData(categoryName, Brushes.LightSkyBlue.Color);
            long categoryId = new CategoryModel(categoryData).Save();
            categoryList.Children.Add(new CategoryOptions(CategoryModel.FindById(categoryId)));
        }

        private void ScrollViewerOnPreviewMouseWheel(object sender, MouseWheelEventArgs e)
        {
            var scv = sender as ScrollViewer;
            if (scv == null) return;
            scv.ScrollToVerticalOffset(scv.VerticalOffset - e.Delta);
            e.Handled = true;
        }
    }
}
