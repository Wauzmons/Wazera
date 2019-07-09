using System;
using System.Linq;
using Wazera.Data;

namespace Wazera
{
    class WazeraTester
    {
        private static Random random = new Random();

        private static string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        public static ProjectData GetMockProject()
        {
            ProjectData projectData = new ProjectData(0, "TEST", "Simple Example Project", GetMockUser());
            projectData.Backlog = FillWithMockTasks(new StatusData("Backlog", projectData), 55);
            projectData.Statuses.Add(FillWithMockTasks(new StatusData("Planned", projectData, 5, 25), 20));
            projectData.Statuses.Add(FillWithMockTasks(new StatusData("In Progress", projectData, 1, 5), 3));
            projectData.Statuses.Add(FillWithMockTasks(new StatusData("Done", projectData, 0, 20), 12));
            return projectData;
        }

        public static UserData GetMockUser()
        {
            UserData user = new UserData(0, "Test", "Peter", "Penguin", WazeraUtils.GetResource("default_avatar.png"));
            LoggedIn.User = user;
            return user;
        }

        public static StatusData FillWithMockTasks(StatusData status, int amount)
        {
            for (int index = 0; index < amount; index++)
            {
                string randomText = new string(Enumerable.Repeat(chars, random.Next(16) + 3)
                    .Select(s => s[random.Next(s.Length)]).ToArray());

                PriorityData priority = PriorityData.Priorities
                    [random.Next(PriorityData.Priorities.Count)];
                
                status.Tasks.Add(new TaskData(123, randomText, status, priority));
            }
            return status;
        }
    }
}
