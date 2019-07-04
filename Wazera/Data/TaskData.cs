namespace Wazera.Data
{
    public class TaskData
    {
        public string Name { get; set; }

        public PriorityData Priority { get; set; }

        public TaskData(string name)
        {
            Name = name;
            Priority = PriorityData.Normal;
        }

        public TaskData(string name, PriorityData priority)
        {
            Name = name;
            Priority = priority;
        }
    }
}
