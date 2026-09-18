package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * The Build Your Own World engine: main menu, game loop, HUD, saving and
 * loading, plus a vision radius effect.
 *
 * Controls:
 *   Main menu: N = new game, L = load game, Q = quit.
 *   Seed entry: type digits, S to start.
 *   In game: WASD to move, T to interact with the tile in front of you,
 *            :q to save and quit.
 */
public class Main {
    private static final int HUD_HEIGHT = 3;
    private static final int VISION_RADIUS = 7;
    private static final String SAVE_FILE = "save.txt";

    private static TERenderer ter;

    public static void main(String[] args) {
        ter = new TERenderer();
        ter.initialize(World.WIDTH, World.HEIGHT + HUD_HEIGHT, 0, HUD_HEIGHT);
        mainMenu();
    }

    // ---------------------------------------------------------------------
    // Main menu
    // ---------------------------------------------------------------------

    /** Shows the main menu and waits for N, L, or Q. */
    private static void mainMenu() {
        while (true) {
            drawMainMenu();
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (key == 'N') {
                    seedPrompt();
                    return;
                } else if (key == 'L') {
                    World world = loadWorld();
                    if (world != null) {
                        gameLoop(world);
                        return;
                    }
                } else if (key == 'Q') {
                    System.exit(0);
                }
            }
            StdDraw.pause(50);
        }
    }

    private static void drawMainMenu() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        Font big = new Font("Monaco", Font.BOLD, 36);
        StdDraw.setFont(big);
        StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 + 6, "CS61B: Build Your Own World");
        Font small = new Font("Monaco", Font.PLAIN, 18);
        StdDraw.setFont(small);
        StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0, "New Game (N)");
        StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 - 2, "Load Game (L)");
        StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 - 4, "Quit (Q)");
        StdDraw.show();
    }

    /** Prompts the user for a numeric seed; S starts the game. */
    private static void seedPrompt() {
        StringBuilder seed = new StringBuilder();
        while (true) {
            StdDraw.clear(Color.black);
            StdDraw.setPenColor(Color.white);
            Font font = new Font("Monaco", Font.BOLD, 24);
            StdDraw.setFont(font);
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 + 4, "Enter a seed");
            Font small = new Font("Monaco", Font.PLAIN, 18);
            StdDraw.setFont(small);
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0, seed.toString());
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 - 3, "Type digits, then press S to start");
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (key == 'S' && seed.length() > 0) {
                    long worldSeed = Long.parseLong(seed.toString());
                    gameLoop(new World(worldSeed));
                    return;
                } else if (Character.isDigit(key) && seed.length() < 18) {
                    seed.append(key);
                } else if (key == '\b' && seed.length() > 0) {
                    seed.deleteCharAt(seed.length() - 1);
                }
            }
            StdDraw.pause(50);
        }
    }

    // ---------------------------------------------------------------------
    // Game loop
    // ---------------------------------------------------------------------

    private static void gameLoop(World world) {
        while (true) {
            renderWorldWithVision(world);
            renderHud(world);
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (key == 'W') {
                    world.moveAvatar(0, 1);
                } else if (key == 'S') {
                    world.moveAvatar(0, -1);
                } else if (key == 'A') {
                    world.moveAvatar(-1, 0);
                } else if (key == 'D') {
                    world.moveAvatar(1, 0);
                } else if (key == 'T') {
                    world.interactWithFrontTile();
                } else if (key == ':') {
                    // Wait for the q that completes the save-and-quit combo.
                    if (waitForSaveKey()) {
                        saveWorld(world);
                        mainMenu();
                        return;
                    }
                }
            }
            StdDraw.pause(30);
        }
    }

    /** Waits briefly for a 'Q' key press; returns true if it arrives. */
    private static boolean waitForSaveKey() {
        for (int i = 0; i < 100; i++) {
            if (StdDraw.hasNextKeyTyped()) {
                return Character.toUpperCase(StdDraw.nextKeyTyped()) == 'Q';
            }
            StdDraw.pause(20);
        }
        return false;
    }

    /** Renders the world, hiding tiles outside the avatar's vision radius. */
    private static void renderWorldWithVision(World world) {
        TETile[][] tiles = world.getTiles();
        TETile[][] view = new TETile[World.WIDTH][World.HEIGHT];
        int ax = world.avatarX();
        int ay = world.avatarY();
        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {
                if (Math.abs(x - ax) <= VISION_RADIUS && Math.abs(y - ay) <= VISION_RADIUS) {
                    view[x][y] = tiles[x][y];
                } else {
                    view[x][y] = World.HIDDEN;
                }
            }
        }
        view[ax][ay] = Tileset.AVATAR;
        ter.drawTiles(view);
    }

    /** Draws the HUD: gold count, seed, controls, and hovered tile info. */
    private static void renderHud(World world) {
        StdDraw.setPenColor(Color.black);
        StdDraw.filledRectangle(World.WIDTH / 2.0, 1.5, World.WIDTH / 2.0, 1.5);

        StdDraw.setPenColor(Color.white);
        Font font = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setFont(font);
        StdDraw.textLeft(0.5, 2.5, "Gold: " + world.goldCount() + "    Seed: " + world.seed());
        StdDraw.textRight(World.WIDTH - 0.5, 2.5,
                "WASD move | T interact | :q save & quit");

        // Hovered tile description.
        String tileInfo = hoveredTileInfo(world);
        Font small = new Font("Monaco", Font.PLAIN, 12);
        StdDraw.setFont(small);
        StdDraw.textLeft(0.5, 0.8, tileInfo);
    }

    private static String hoveredTileInfo(World world) {
        int tileX = (int) Math.floor(StdDraw.mouseX());
        int tileY = (int) Math.floor(StdDraw.mouseY()) - HUD_HEIGHT;
        TETile[][] tiles = world.getTiles();
        if (tileX >= 0 && tileX < World.WIDTH && tileY >= 0 && tileY < World.HEIGHT) {
            int ax = world.avatarX();
            int ay = world.avatarY();
            if (Math.abs(tileX - ax) > VISION_RADIUS || Math.abs(tileY - ay) > VISION_RADIUS) {
                return "Tile: hidden";
            }
            return "Tile: " + tiles[tileX][tileY].description();
        }
        return "Tile: ";
    }

    // ---------------------------------------------------------------------
    // Saving and loading
    // ---------------------------------------------------------------------

    /** Saves the current world state to SAVE_FILE. */
    private static void saveWorld(World world) {
        try (FileWriter writer = new FileWriter(SAVE_FILE)) {
            writer.write(world.seed() + "\n");
            writer.write(world.avatarX() + " " + world.avatarY() + "\n");
            writer.write(world.goldCount() + "\n");
            writer.write(String.join(" ", world.collectedGold()) + "\n");
        } catch (IOException e) {
            System.err.println("Could not save game: " + e.getMessage());
        }
    }

    /** Loads the world state from SAVE_FILE, or returns null if there is no
     *  valid save. */
    private static World loadWorld() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return null;
        }
        try (Scanner scanner = new Scanner(file)) {
            long seed = Long.parseLong(scanner.nextLine());
            String[] position = scanner.nextLine().split(" ");
            int avatarX = Integer.parseInt(position[0]);
            int avatarY = Integer.parseInt(position[1]);
            int goldCount = Integer.parseInt(scanner.nextLine());
            Set<String> collected = new HashSet<>();
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.isEmpty()) {
                    for (String loc : line.split(" ")) {
                        collected.add(loc);
                    }
                }
            }
            World world = new World(seed);
            world.setAvatarPosition(avatarX, avatarY);
            world.setGoldCount(goldCount);
            world.applyCollectedGold(collected);
            return world;
        } catch (FileNotFoundException e) {
            return null;
        } catch (RuntimeException e) {
            System.err.println("Save file is corrupted: " + e.getMessage());
            return null;
        }
    }
}
