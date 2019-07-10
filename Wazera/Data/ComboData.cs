using System.Windows.Controls;

namespace Wazera.Data
{
    class ComboData<T> : ComboBoxItem
    {
        public T Value { get; set; }

        public ComboData(T value)
        {
            Value = value;
        }
    }
}
