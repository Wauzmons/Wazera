namespace Wazera.Data
{
    public class StatusData
    {
        public string Title { get; set; }

        public int MinCards { get; set; }

        public int MaxCards { get; set; }

        public StatusData(string title)
        {
            Title = title;
            MinCards = 0;
            MaxCards = 0;
        }

        public StatusData(string title, int minCards, int maxCards)
        {
            Title = title;
            MinCards = minCards;
            MaxCards = maxCards;
        }

        public bool HasCardMinimum()
        {
            return MinCards != 0;
        }

        public bool HasCardMaximum()
        {
            return MaxCards != 0;
        }
    }
}
