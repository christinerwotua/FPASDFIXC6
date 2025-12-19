import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class GamePanel extends JPanel {
    private final MainGameGUI host;

    public GamePanel(MainGameGUI host) {
        this.host = host;
        setOpaque(false);
    }

    public Point getCoordinates(int id) {
        int cols = 8, rows = 8;
        int w = getWidth() / cols;
        int h = getHeight() / rows;

        int mathRow = (id - 1) / 8;
        int col = (mathRow % 2 == 0) ? (id - 1) % 8 : 7 - ((id - 1) % 8);
        return new Point(col * w + w / 2, (7 - mathRow) * h + h / 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Board board = host.getGameBoard();
        Queue<Player> turnQueue = host.getTurnQueue();

        int cols = 8, rows = 8;
        int w = getWidth() / cols;
        int h = getHeight() / rows;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * w;
                int y = r * h;

                int mathRow = 7 - r;
                int id = (mathRow % 2 == 0) ? (mathRow * 8) + c + 1 : (mathRow * 8) + (7 - c) + 1;
                drawTile(g2, x, y, w, h, id, board);
            }
        }

        // draw shortcuts
        for (int i = 1; i <= 64; i++) {
            Node n = board.getNodeById(i);
            if (n != null && n.shortcut != null) {
                Point p1 = getCoordinates(n.id);
                Point p2 = getCoordinates(n.shortcut.id);
                drawLadder(g2, p1.x, p1.y, p2.x, p2.y);

                boolean open = false;
                Color lockColor = new Color(160, 160, 160);
                if (n.id == host.getActiveLockNodeId()) {
                    open = host.isLockOpen();
                    if (host.isLockShaking()) lockColor = new Color(220, 80, 80);
                }
                drawScanner(g2, p1.x - 10, p1.y - 15, open, lockColor);
            }
        }

        // players
        if (turnQueue != null) {
            Map<Integer, List<Player>> onNode = new HashMap<>();
            for (Player p : turnQueue) onNode.computeIfAbsent(p.getCurrentPosition().id, k -> new ArrayList<>()).add(p);

            for (Integer nodeId : onNode.keySet()) {
                List<Player> occ = onNode.get(nodeId);
                Point pos = getCoordinates(nodeId);

                int spacing = 18;
                int startX = pos.x - ((occ.size() - 1) * spacing) / 2;

                for (int i = 0; i < occ.size(); i++) {
                    Player p = occ.get(i);
                    if (p == host.getAnimatingPlayer() && host.getCurrentAnimPos() != null) continue;
                    drawPlayerIcon(g2, p, startX + i * spacing, pos.y);
                }
            }

            if (host.getAnimatingPlayer() != null && host.getCurrentAnimPos() != null) {
                Point2D.Double ap = host.getCurrentAnimPos();
                drawPlayerIcon(g2, host.getAnimatingPlayer(), (int) ap.x, (int) ap.y);
            }
        }
    }

    private void drawTile(Graphics2D g2, int x, int y, int w, int h, int id, Board board) {
        // tiles tidak putih: gelap & nyaman (bukan neon)
        Color tile = new Color(44, 52, 66);
        Color border = new Color(92, 104, 122);

        g2.setColor(tile);
        g2.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 10, 10);

        g2.setColor(border);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x + 2, y + 2, w - 4, h - 4, 10, 10);

        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(new Color(15, 20, 30, 160));
        g2.drawString(String.valueOf(id), x + 10, y + 22);

        if (board.isPrime(id)) drawPrimeBadge(g2, x + w - 20, y + 12);
        if (id % 5 == 0 && id != 64) drawCrateMark(g2, x + w - 16, y + h - 16);

        if (board.getPointsAt(id) > 0) drawPawpsicleMark(g2, x + 26, y + h - 18);

        if (id == 64) drawFinish(g2, x, y, w, h);
    }

    private void drawFinish(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(0,0,0,80));
        g2.fillRoundRect(x + 6, y + h / 2 - 12, w - 12, 22, 12, 12);

        g2.setColor(new Color(235, 220, 170));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(x + 6, y + h / 2 - 12, w - 12, 22, 12, 12);

        g2.setFont(new Font("Impact", Font.PLAIN, 14));
        g2.setColor(new Color(245, 245, 245));
        String label = "CITY HALL";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(label, x + (w - fm.stringWidth(label)) / 2, y + h / 2 + 5);
    }

    private void drawPrimeBadge(Graphics2D g2, int cx, int cy) {
        g2.setColor(new Color(235, 200, 120));
        int s = 12;
        int[] xP = {cx, cx + s/2, cx, cx - s/2};
        int[] yP = {cy - s/2, cy, cy + s/2, cy};
        g2.fillPolygon(xP, yP, 4);
        g2.setColor(new Color(70, 85, 110));
        g2.setStroke(new BasicStroke(2f));
        g2.drawPolygon(xP, yP, 4);
    }

    private void drawCrateMark(Graphics2D g2, int cx, int cy) {
        g2.setColor(new Color(210, 170, 120));
        g2.fillRoundRect(cx - 6, cy - 6, 12, 12, 4, 4);
        g2.setColor(new Color(80, 65, 45));
        g2.drawRoundRect(cx - 6, cy - 6, 12, 12, 4, 4);
    }

    private void drawPawpsicleMark(Graphics2D g2, int cx, int cy) {
        g2.setColor(new Color(230, 90, 110));
        g2.fillOval(cx - 7, cy - 7, 14, 14);
        g2.setColor(new Color(120, 55, 55));
        g2.drawOval(cx - 7, cy - 7, 14, 14);
    }

    private void drawLadder(Graphics2D g2, int x1, int y1, int x2, int y2) {
        Stroke old = g2.getStroke();
        Composite oc = g2.getComposite();

        double dx = x2 - x1, dy = y2 - y1;
        double len = Math.hypot(dx, dy);
        if (len < 1) return;

        double ux = dx / len, uy = dy / len;
        double nx = -uy, ny = ux;

        double pad = 22;
        double sx = x1 + ux * pad, sy = y1 + uy * pad;
        double ex = x2 - ux * pad, ey = y2 - uy * pad;

        double ladderLen = Math.hypot(ex - sx, ey - sy);
        if (ladderLen < 1) return;

        double halfWidth = 7;

        double r1x1 = sx + nx * halfWidth, r1y1 = sy + ny * halfWidth;
        double r1x2 = ex + nx * halfWidth, r1y2 = ey + ny * halfWidth;

        double r2x1 = sx - nx * halfWidth, r2y1 = sy - ny * halfWidth;
        double r2x2 = ex - nx * halfWidth, r2y2 = ey - ny * halfWidth;

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        g2.setColor(new Color(235, 235, 240));
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new Line2D.Double(r1x1, r1y1, r1x2, r1y2));
        g2.draw(new Line2D.Double(r2x1, r2y1, r2x2, r2y2));

        int rungCount = Math.max(4, (int) (ladderLen / 22));
        double step = ladderLen / rungCount;
        double ldx = (ex - sx) / ladderLen, ldy = (ey - sy) / ladderLen;

        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 1; i < rungCount; i++) {
            double px = sx + ldx * (i * step);
            double py = sy + ldy * (i * step);

            double ax = px + nx * (halfWidth - 0.5);
            double ay = py + ny * (halfWidth - 0.5);
            double bx = px - nx * (halfWidth - 0.5);
            double by = py - ny * (halfWidth - 0.5);

            g2.draw(new Line2D.Double(ax, ay, bx, by));
        }

        g2.setComposite(oc);
        g2.setStroke(old);
    }

    private void drawScanner(Graphics2D g2, int x, int y, boolean open, Color c) {
        g2.setColor(new Color(30, 34, 42));
        g2.fillRoundRect(x, y, 20, 26, 6, 6);

        g2.setColor(open ? new Color(120, 210, 155) : c);
        g2.fillOval(x + 5, y + 5, 10, 10);

        g2.setColor(new Color(255,255,255,80));
        g2.drawRoundRect(x, y, 20, 26, 6, 6);
    }

    // ICON PLAYER KOTAK (lebih rapih, tidak nutupin)
    private void drawPlayerIcon(Graphics2D g2, Player p, int cx, int cy) {
        int size = 16;     // kecil biar gak nutupin board
        int x = cx - size/2;
        int y = cy - size/2;

        // shadow
        g2.setColor(new Color(0,0,0,70));
        g2.fillRoundRect(x + 2, y + 2, size, size, 4, 4);

        // base box
        g2.setColor(p.getColor());
        g2.fillRoundRect(x, y, size, size, 4, 4);

        // draw image kalau ada
        if (p.getIcon() != null) {
            Shape oldClip = g2.getClip();
            g2.setClip(new RoundRectangle2D.Double(x, y, size, size, 4, 4));
            g2.drawImage(p.getIcon(), x, y, size, size, null);
            g2.setClip(oldClip);
        } else {
            // fallback: inisial (biar tetap kebaca)
            g2.setFont(new Font("Arial", Font.BOLD, 9));
            g2.setColor(new Color(20,20,20,180));
            String s = p.getName().trim().isEmpty() ? "?" : ("" + Character.toUpperCase(p.getName().charAt(0)));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(s, x + (size - fm.stringWidth(s))/2, y + (size + fm.getAscent())/2 - 1);
        }

        // border
        g2.setColor(new Color(15, 18, 25, 170));
        g2.drawRoundRect(x, y, size, size, 4, 4);
    }
}
