import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class StartMenuGUI extends JFrame {

    private static final Color BG_TOP    = new Color(12, 14, 22);
    private static final Color BG_BOTTOM = new Color(22, 26, 36);

    private static final Color GOLD      = new Color(235, 200, 120);
    private static final Color TEXT      = new Color(245, 248, 255);
    private static final Color MUTED     = new Color(190, 200, 220);

    private static final Color BTN_ACCENT= new Color(190, 140, 90);
    private static final Color BTN_MAIN  = new Color(70, 120, 170);
    private static final Color BTN_EXIT  = new Color(200, 95, 95);

    // optional image: taruh "menu_bg.png" di folder project
    private BufferedImage menuBg;

    public StartMenuGUI() {
        setTitle("Zootopia: Pursuit to City Hall");
        setSize(980, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // load background image optional
        menuBg = tryLoadImage("menu_bg.png");

        JPanel root = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOTTOM);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // draw image transparent jika ada
                if (menuBg != null) {
                    Composite old = g2.getComposite();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                    g2.drawImage(menuBg, 0, 0, getWidth(), getHeight(), null);
                    g2.setComposite(old);
                }

                // soft overlay
                g2.setColor(new Color(0,0,0,70));
                g2.fillRect(0,0,getWidth(), getHeight());
            }
        };
        root.setLayout(new BorderLayout());
        root.setBorder(new EmptyBorder(26, 26, 26, 26));
        setContentPane(root);

        // left hero
        JPanel hero = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setFont(new Font("Impact", Font.PLAIN, 56));
                g2.setColor(GOLD);
                g2.drawString("ZOOTOPIA", 22, 88);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
                g2.setColor(TEXT);
                g2.drawString("Pursuit to City Hall", 24, 128);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                g2.setColor(new Color(220, 230, 245));
                g2.drawString("Green: forward • Red: back • Prime-start: shortcut access • Finish: 64", 24, 158);

                // simple badge
                int cx = 60, cy = 218;
                g2.setColor(GOLD);
                int s = 32;
                int[] xP = {cx, cx + s/2, cx, cx - s/2};
                int[] yP = {cy - s/2, cy, cy + s/2, cy};
                g2.fillPolygon(xP, yP, 4);

                g2.setColor(new Color(70, 85, 110));
                g2.setStroke(new BasicStroke(2f));
                g2.drawPolygon(xP, yP, 4);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g2.setColor(new Color(220, 225, 235));
                g2.drawString("ZPD Training Simulation", 96, 224);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.setColor(MUTED);
                g2.drawString("Tip: Put .wav files in the same folder as .java files.", 96, 246);
            }
        };
        hero.setOpaque(false);
        hero.setPreferredSize(new Dimension(560, 0));
        root.add(hero, BorderLayout.CENTER);

        // right menu card
        JPanel right = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

                g2.setColor(new Color(255, 255, 255, 60));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 24, 24);
            }
        };
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(24, 24, 24, 24));
        right.setPreferredSize(new Dimension(320, 0));
        root.add(right, BorderLayout.EAST);

        JLabel menuTitle = new JLabel("MAIN MENU", SwingConstants.CENTER);
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        menuTitle.setForeground(new Color(220, 225, 235));

        JLabel hint = new JLabel(
                "<html><div style='text-align:center;'>Start game, choose mode,<br/>and chase to City Hall!</div></html>",
                SwingConstants.CENTER
        );
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hint.setForeground(new Color(220, 230, 245));

        JButton btnPlay = makeButton("PLAY", BTN_ACCENT, TEXT);
        btnPlay.addActionListener(e -> {
            setVisible(false);
            new MainGameGUI(this);
        });

        JButton btnHow = makeButton("HOW TO PLAY", BTN_MAIN, TEXT);
        btnHow.addActionListener(e -> showHowToPlayDialog());

        JButton btnExit = makeButton("EXIT", BTN_EXIT, TEXT);
        btnExit.addActionListener(e -> System.exit(0));

        right.add(menuTitle);
        right.add(Box.createVerticalStrut(10));
        right.add(hint);
        right.add(Box.createVerticalStrut(22));
        right.add(btnPlay);
        right.add(Box.createVerticalStrut(10));
        right.add(btnHow);
        right.add(Box.createVerticalStrut(10));
        right.add(btnExit);
        right.add(Box.createVerticalGlue());

        setVisible(true);
    }

    private BufferedImage tryLoadImage(String name) {
        try {
            File f = new File(name);
            if (!f.exists()) f = new File(System.getProperty("user.dir"), name);
            if (!f.exists()) return null;
            return ImageIO.read(f);
        } catch (Exception e) {
            return null;
        }
    }

    private void showHowToPlayDialog() {
        JOptionPane.showMessageDialog(
                this,
                "Rules:\n" +
                        "- Green dice: move forward\n" +
                        "- Red dice: move backward\n" +
                        "- Prime-start: shortcut access\n" +
                        "- Land on multiples of 5: bonus turn\n" +
                        "- Pawpsicles: score\n" +
                        "- Reach 64: win",
                "HOW TO PLAY",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            boolean hover = false;
            boolean pressed = false;

            {
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setOpaque(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setHorizontalAlignment(SwingConstants.CENTER);

                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { hover = false; pressed = false; repaint(); }
                    @Override public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
                    @Override public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
                });
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color base = isEnabled() ? bg : new Color(120, 130, 150);
                if (hover && isEnabled()) base = base.brighter();
                if (pressed && isEnabled()) base = base.darker();

                int arc = 18;
                int shadowX = 3, shadowY = 5;

                g2.setColor(new Color(0, 0, 0, 90));
                g2.fillRoundRect(2 + shadowX, 4 + shadowY, getWidth() - 6, getHeight() - 6, arc, arc);

                g2.setColor(base);
                g2.fillRoundRect(2, 4, getWidth() - 6, getHeight() - 6, arc, arc);

                g2.setColor(new Color(255, 255, 255, 90));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(2, 4, getWidth() - 6, getHeight() - 6, arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(999, 46));
        b.setPreferredSize(new Dimension(220, 46));
        b.setMargin(new Insets(12, 12, 12, 12));
        return b;
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        SwingUtilities.invokeLater(StartMenuGUI::new);
    }
}
