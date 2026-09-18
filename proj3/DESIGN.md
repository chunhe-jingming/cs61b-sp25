# Proj 3 Design Document: Build Your Own World

## Overview

BYOW is a 2D tile-based world exploration engine. The world is generated
from a seed, the player explores it with keyboard controls, collects gold,
and can save/load the game state. This document describes the design and
its rationale.

## Architecture

Two classes in `core`:

- **`World`** — the model. Owns the tile grid, the room list, the avatar
  position, and gold state. All world mutation happens here.
- **`Main`** — the controller/view. Owns the menu, the game loop, HUD
  rendering, and persistence. It reads input and calls into `World`.

`tileengine` and `utils` are provided libraries (renderer, tiles, random
helpers).

## World Generation (Task 3)

Generation runs in five phases, all driven by a single `Random` seeded
from the user's seed so the same seed always produces the same world:

1. **Fill** the grid with `NOTHING`.
2. **Rooms**: up to 40 non-overlapping rectangles (4-11 x 4-9) are placed
   by rejection sampling, each filled with `FLOOR`.
3. **Hallways**: every consecutive room pair is connected by an L-shaped
   hallway from center to center (horizontal-first or vertical-first,
   chosen randomly).
4. **Walls**: every `NOTHING` tile adjacent to a `FLOOR` tile becomes
   `WALL`. This single post-pass handles room borders, hallway borders,
   and wall thinning without any special cases.
5. **Decoration**: empty space is sprinkled with grass, flowers, trees,
   sand, and a few small ponds (`WATER`, which blocks movement).

Why rejection sampling for rooms: it is simple, gives natural-looking
variety, and with 2000 attempts the grid never runs out of space in
practice. Why the wall post-pass: generating walls alongside rooms and
hallways directly is notoriously fiddly (walls get overwritten by
hallways, corners get missed); deriving walls from the final floor shape
is one simple rule instead of three.

### Ambition feature: vision radius (Task 8)

The world is ~70x42 but the player only sees an 15x15 window around the
avatar; everything else renders as black. This makes exploration feel
rewarding (the map is not spoiled) and is implemented as a cheap copy
of the tile array before each frame.

## Interactivity (Task 5)

- WASD moves the avatar; movement is only allowed onto `FLOOR` and
  `GOLD` tiles (`WATER` blocks).
- T interacts with the tile in front of the avatar. The only interactive
  tile is `GOLD`: interacting collects it (tile becomes `FLOOR`, gold
  counter increments, location recorded).

Design choice: the avatar always faces "up" (the direction of its last
move is not tracked). This keeps the interaction rule trivially
predictable for the player; tracking facing direction was considered
but adds state for little gameplay benefit.

## HUD (Task 6)

The window is initialized with 3 extra tiles at the top. The HUD shows:
gold collected, current seed, control hints, and — from `StdDraw` mouse
position — the description of the hovered tile ("Tile: wall", "Tile:
gold", ...). The hovered tile respects the vision radius and reports
"hidden" for unexplored tiles.

## Saving and Loading (Task 7)

`:q` writes `save.txt`:

```
<seed>
<avatarX> <avatarY>
<goldCount>
<collectedGoldLocations...>
```

Only the seed plus deltas are stored — the world is regenerated
deterministically from the seed, and collected gold locations are
re-applied to the fresh world. This keeps the save file tiny and avoids
serializing the tile grid. Loading (`L` on the menu) reconstructs the
world and restores avatar position and gold state.

## Other Design Decisions

- **`TETile` immutability**: tiles are singletons from `Tileset` (plus a
  custom `GOLD` and `HIDDEN` tile). Comparisons use reference equality
  (`==`), which is safe because tiles are never mutated.
- **`Random` over `RandomUtils`**: world generation only needs uniform
  ints and booleans, so the JDK `Random` suffices and removes a
  dependency.
- **No monster/AI feature**: deliberately out of scope. The vision
  radius and gold collection were chosen as the ambition features
  because they integrate cleanly with the rendering and interaction
  pipeline without adding a second simulation loop.

## Testing Strategy

World generation is verified by an external harness (`VerifyWorld`)
that, for several seeds, checks:
- the avatar spawns on a walkable tile;
- every floor/gold tile is reachable from the avatar (BFS);
- a minimum number of gold pieces exists.

Saving/loading is round-trip tested by saving a world state, mutating
the world, and checking that loading restores the exact prior state.
