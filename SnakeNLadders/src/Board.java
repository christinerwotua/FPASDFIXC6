import java.util.*;

public class Board {
    private final Map<Integer, Node> nodes = new HashMap<>();
    private final Map<Integer, Integer> points = new HashMap<>();
    private final Random rand = new Random();

    public Board() {
        buildNodes();
        randomizeShortcuts();
        randomizePoints();
    }

    private void buildNodes() {
        for (int i = 1; i <= 64; i++) nodes.put(i, new Node(i));
        for (int i = 1; i < 64; i++) nodes.get(i).next = nodes.get(i + 1);
    }

    private void randomizeShortcuts() {
        // bikin beberapa shortcut acak (naik), tapi aman dan nggak nabrak finish
        // (kamu bisa ubah jumlahnya kalau mau)
        int shortcutCount = 7;

        Set<Integer> usedStart = new HashSet<>();
        Set<Integer> usedEnd = new HashSet<>();

        int tries = 0;
        while (usedStart.size() < shortcutCount && tries < 5000) {
            tries++;

            int start = 2 + rand.nextInt(58); // 2..59
            int end = start + (3 + rand.nextInt(12)); // +3..+14
            if (end > 63) continue;

            // jangan bikin shortcut dari node 64 / ke 64 biar visual enak
            if (start == 64 || end == 64) continue;

            // jangan tabrakan
            if (usedStart.contains(start) || usedEnd.contains(end)) continue;

            // jangan bikin shortcut terlalu dekat
            if (Math.abs(end - start) < 3) continue;

            usedStart.add(start);
            usedEnd.add(end);

            nodes.get(start).shortcut = nodes.get(end);
        }
    }

    private void randomizePoints() {
        // taruh pawpsicles di beberapa node acak
        // jangan taruh di 1 dan 64
        int count = 12;

        Set<Integer> used = new HashSet<>();
        int tries = 0;
        while (used.size() < count && tries < 5000) {
            tries++;
            int id = 2 + rand.nextInt(62); // 2..63
            if (id == 64 || id == 1) continue;
            if (nodes.get(id).shortcut != null) continue; // biar gak numpuk sama shortcut start
            if (used.contains(id)) continue;

            int val = 5 + rand.nextInt(16); // 5..20
            used.add(id);
            points.put(id, val);
        }
    }

    public Node getNodeById(int id) { return nodes.get(id); }
    public Node getStartNode() { return nodes.get(1); }

    public int getPointsAt(int id) { return points.getOrDefault(id, 0); }
    public void removePoints(int id) { points.remove(id); }

    public boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
