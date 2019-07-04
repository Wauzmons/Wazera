using System;
using System.Linq;
using Wazera.Data;

namespace Wazera.Kanban
{
    public class KanbanTester
    {
        private static Random random = new Random();

        private static string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        public static KanbanBoard GetMockBoard()
        {
            KanbanBoard kanbanBoard = new KanbanBoard();
            AddMockColumn(kanbanBoard, "Planned");
            AddMockColumn(kanbanBoard, "In Progress");
            AddMockColumn(kanbanBoard, "Done");
            return kanbanBoard;
        }

        public static KanbanColumn AddMockColumn(KanbanBoard kanbanBoard, string columnTitle)
        {
            KanbanColumn column = kanbanBoard.AddColumn(new StatusData(columnTitle, 15, 25));

            for (int index = 0; index < 20; index++)
            {
                string randomText = new string(Enumerable.Repeat(chars, random.Next(16) + 3)
                    .Select(s => s[random.Next(s.Length)]).ToArray());

                PriorityData priority = null;
                switch (random.Next(5) + 1)
                {
                    case 1:
                        priority = PriorityData.Critical;
                        break;
                    case 2:
                        priority = PriorityData.High;
                        break;
                    case 3:
                        priority = PriorityData.Normal;
                        break;
                    case 4:
                        priority = PriorityData.Low;
                        break;
                    case 5:
                        priority = PriorityData.Insignificant;
                        break;
                }
                column.AddRow(new TaskData(randomText, priority));
            }
            return column;
        }
    }
}
