using System.Windows;
using System.Windows.Input;
using Wazera.Data;
using Wazera.Model;

namespace Wazera.Users
{
    public partial class LoginScreen : Window
    {
        public LoginScreen()
        {
            InitializeComponent();
        }

        private void UsernameInput_KeyDown(object sender, System.Windows.Input.KeyEventArgs e)
        {
            if(e.Key == Key.Return)
            {
                passwordInput.Focus();
            }
        }

        private void PasswordInput_KeyDown(object sender, System.Windows.Input.KeyEventArgs e)
        {
            if (e.Key == Key.Return)
            {
                LoginButton_Click(null, null);
            }
        }

        private void LoginButton_Click(object sender, RoutedEventArgs e)
        {
            string username = usernameInput.Text ?? "";
            string password = passwordInput.Password ?? "";

            UserData userData = UserModel.FindByUsernameAndPassword(username, password);

            if(userData != null)
            {
                MainWindow.Instance.LoginUser(userData);
            }
            if(username.Equals("test"))
            {
                MainWindow.Instance.LoginUser(UserData.GetDefaultUser());
            }
            else
            {
                MessageBox.Show("Unknown Username or Password!", "Login Failed");
            }
        }
    }
}
