import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {
    private final String name;
    private Node currentPosition;
    private final Color color;
    private final boolean bot;

    private int score = 0;
    private int lastPositionId = 1;

    // optional icon
    private BufferedImage icon;

    public Player(String name, Node start, Color color, boolean bot) {
        this.name = name;
        this.currentPosition = start;
        this.color = color;
        this.bot = bot;
    }

    public String getName() { return name; }
    public Node getCurrentPosition() { return currentPosition; }
    public void setPosition(Node n) { this.currentPosition = n; }
    public Color getColor() { return color; }
    public boolean isBot() { return bot; }

    public int getScore() { return score; }
    public void addScore(int pts) { score += pts; }

    public int getLastPositionId() { return lastPositionId; }
    public void setLastPositionId(int id) { lastPositionId = id; }

    public void stepForward() {
        if (currentPosition != null && currentPosition.next != null) {
            currentPosition = currentPosition.next;
        }
    }

    public BufferedImage getIcon() { return icon; }
    public void setIcon(BufferedImage icon) { this.icon = icon; }
}
