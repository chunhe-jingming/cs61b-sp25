package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * A randomly generated, tile-based world. Worlds are generated from a seed
 * and consist of rooms connected by hallways, decorated with terrain tiles,
 * and sprinkled with collectible gold.
 */
public class World {

    /** Width of the world in tiles. */
    public static final int WIDTH = 70;
    /** Height of the world in tiles. */
    public static final int HEIGHT = 42;

    /** Gold tile: collectible, walkable. */
    public static final TETile GOLD = new TETile('$', new Color(255, 215, 0), Color.black,
            "gold", 13);
    /** Hidden tile used for the vision radius effect. */
    public static final TETile HIDDEN = new TETile(' ', Color.black, Color.black,
            "hidden", 99);

    /** A rectangle describing one room in the world. */
    private static class Rect {
        int x;
        int y;
        int w;
        int h;

        Rect(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        int centerX() {
            return x + w / 2;
        }

        int centerY() {
            return y + h / 2;
        }

        boolean overlaps(Rect other) {
            return x < other.x + other.w + 1 && x + w + 1 > other.x
                    && y < other.y + other.h + 1 && y + h + 1 > other.y;
        }
    }

    private final long seed;
    private final Random random;
    private final TETile[][] tiles = new TETile[WIDTH][HEIGHT];
    private final List<Rect> rooms = new ArrayList<>();
    private final Set<String> collectedGold = new HashSet<>();
    private int goldCount;
    private int avatarX;
    private int avatarY;

    /** Creates a new world from the given seed. */
    public World(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
        generateWorld();
    }

    /** Returns the seed used to generate this world. */
    public long seed() {
        return seed;
    }

    /** Returns the 2D array of tiles. */
    public TETile[][] getTiles() {
        return tiles;
    }

    /** Returns the avatar's current x position. */
    public int avatarX() {
        return avatarX;
    }

    /** Returns the avatar's current y position. */
    public int avatarY() {
        return avatarY;
    }

    /** Returns the number of gold pieces collected so far. */
    public int goldCount() {
        return goldCount;
    }

    /** Returns the locations of already-collected gold as "x,y" strings. */
    public Set<String> collectedGold() {
        return collectedGold;
    }

    /** Removes the gold at every location in collected so that loading a save
     *  does not resurrect collected gold. */
    public void applyCollectedGold(Set<String> collected) {
        for (String loc : collected) {
            String[] parts = loc.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            if (tiles[x][y] == GOLD) {
                tiles[x][y] = Tileset.FLOOR;
            }
        }
        collectedGold.addAll(collected);
    }

    /** Restores the avatar position (used when loading a save). */
    public void setAvatarPosition(int x, int y) {
        avatarX = x;
        avatarY = y;
    }

    /** Sets the number of gold pieces collected so far (used when loading). */
    public void setGoldCount(int count) {
        goldCount = count;
    }

    /** Attempts to move the avatar by (dx, dy). The avatar can only step on
     *  floor and gold tiles. */
    public void moveAvatar(int dx, int dy) {
        int newX = avatarX + dx;
        int newY = avatarY + dy;
        if (inBounds(newX, newY) && isWalkable(tiles[newX][newY])) {
            avatarX = newX;
            avatarY = newY;
        }
    }

    /** Returns true if the tile in front of the avatar is interactive. */
    public boolean isFrontTileInteractive() {
        int[] front = tileInFront();
        return tiles[front[0]][front[1]] == GOLD;
    }

    /** Interacts with the tile in front of the avatar. Collects gold. */
    public void interactWithFrontTile() {
        int[] front = tileInFront();
        int x = front[0];
        int y = front[1];
        if (tiles[x][y] == GOLD) {
            tiles[x][y] = Tileset.FLOOR;
            goldCount++;
            collectedGold.add(x + "," + y);
        }
    }

    /** Returns the coordinates of the tile directly in front of the avatar. */
    private int[] tileInFront() {
        // The avatar always faces the last direction it moved; by default, up.
        return new int[]{avatarX, avatarY + 1};
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    private boolean isWalkable(TETile tile) {
        return tile == Tileset.FLOOR || tile == GOLD;
    }

    // ---------------------------------------------------------------------
    // World generation
    // ---------------------------------------------------------------------

    private void generateWorld() {
        fillWithNothing();
        generateRooms();
        connectRooms();
        addWallsAroundFloors();
        decorateWorld();
        spawnGold();
        spawnAvatar();
    }

    private void fillWithNothing() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /** Randomly places non-overlapping rooms until MAX_ROOMS rooms exist or
     *  MAX_ATTEMPTS placements fail. */
    private void generateRooms() {
        final int maxRooms = 40;
        final int maxAttempts = 2000;
        int attempts = 0;
        while (rooms.size() < maxRooms && attempts < maxAttempts) {
            attempts++;
            int w = 4 + random.nextInt(8);
            int h = 4 + random.nextInt(6);
            int x = 1 + random.nextInt(WIDTH - w - 2);
            int y = 1 + random.nextInt(HEIGHT - h - 2);
            Rect candidate = new Rect(x, y, w, h);
            boolean overlap = false;
            for (Rect room : rooms) {
                if (room.overlaps(candidate)) {
                    overlap = true;
                    break;
                }
            }
            if (!overlap) {
                rooms.add(candidate);
                drawRoom(candidate);
            }
        }
    }

    /** Draws the floor and wall border of a room. */
    private void drawRoom(Rect room) {
        for (int x = room.x; x < room.x + room.w; x++) {
            for (int y = room.y; y < room.y + room.h; y++) {
                tiles[x][y] = Tileset.FLOOR;
            }
        }
    }

    /** Connects each room to the next with an L-shaped hallway. */
    private void connectRooms() {
        for (int i = 0; i + 1 < rooms.size(); i++) {
            Rect a = rooms.get(i);
            Rect b = rooms.get(i + 1);
            carveHallway(a.centerX(), a.centerY(), b.centerX(), b.centerY());
        }
    }

    /** Carves an L-shaped floor hallway between (x1, y1) and (x2, y2). */
    private void carveHallway(int x1, int y1, int x2, int y2) {
        // Randomly choose whether to go horizontally first or vertically first.
        if (random.nextBoolean()) {
            carveLine(x1, x2, y1, true);
            carveLine(y1, y2, x2, false);
        } else {
            carveLine(y1, y2, x1, false);
            carveLine(x1, x2, y2, true);
        }
    }

    /** Carves a floor line along one axis. If horizontal, moves x from min to
     *  max at fixed y; otherwise moves y at fixed x. */
    private void carveLine(int from, int to, int fixed, boolean horizontal) {
        int lo = Math.min(from, to);
        int hi = Math.max(from, to);
        for (int i = lo; i <= hi; i++) {
            if (horizontal) {
                tiles[i][fixed] = Tileset.FLOOR;
            } else {
                tiles[fixed][i] = Tileset.FLOOR;
            }
        }
    }

    /** Converts any NOTHING tile adjacent to a floor tile into a wall. */
    private void addWallsAroundFloors() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (tiles[x][y] == Tileset.FLOOR) {
                    wallOff(x - 1, y);
                    wallOff(x + 1, y);
                    wallOff(x, y - 1);
                    wallOff(x, y + 1);
                }
            }
        }
    }

    private void wallOff(int x, int y) {
        if (inBounds(x, y) && tiles[x][y] == Tileset.NOTHING) {
            tiles[x][y] = Tileset.WALL;
        }
    }

    /** Sprinkles decorative terrain tiles across the empty space and inside
     *  rooms. Water tiles block movement. */
    private void decorateWorld() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (tiles[x][y] == Tileset.NOTHING) {
                    double r = random.nextDouble();
                    if (r < 0.12) {
                        tiles[x][y] = Tileset.GRASS;
                    } else if (r < 0.17) {
                        tiles[x][y] = Tileset.FLOWER;
                    } else if (r < 0.21) {
                        tiles[x][y] = Tileset.TREE;
                    } else if (r < 0.24) {
                        tiles[x][y] = Tileset.SAND;
                    }
                }
            }
        }
        // A few small ponds in empty space.
        int ponds = 5 + random.nextInt(6);
        for (int p = 0; p < ponds; p++) {
            int cx = 2 + random.nextInt(WIDTH - 4);
            int cy = 2 + random.nextInt(HEIGHT - 4);
            int radius = 1 + random.nextInt(2);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    int x = cx + dx;
                    int y = cy + dy;
                    if (inBounds(x, y) && tiles[x][y] == Tileset.NOTHING
                            && dx * dx + dy * dy <= radius * radius) {
                        tiles[x][y] = Tileset.WATER;
                    }
                }
            }
        }
    }

    /** Places gold on random floor tiles. */
    private void spawnGold() {
        int goldPieces = 8 + random.nextInt(8);
        int placed = 0;
        while (placed < goldPieces) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            if (tiles[x][y] == Tileset.FLOOR) {
                tiles[x][y] = GOLD;
                placed++;
            }
        }
    }

    /** Places the avatar at the center of the first room. */
    private void spawnAvatar() {
        if (!rooms.isEmpty()) {
            avatarX = rooms.get(0).centerX();
            avatarY = rooms.get(0).centerY();
        } else {
            avatarX = WIDTH / 2;
            avatarY = HEIGHT / 2;
        }
    }
}
