package ms.apex.announcer;

public enum Rarity {
    VERY_RARE(0.5),
    RARE(2.0);

    private final double percent;

    Rarity(double percent) {
        this.percent = percent;
    }

    public double getPercent() {
        return percent;
    }

    public String getDisplayName() {
        return String.format("%.1f%%", percent);
    }
}
