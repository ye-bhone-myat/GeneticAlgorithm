package FactoryFloor;

public class Tile {
    private Tile northTile, eastTile, southTile, westTile;
    private boolean occupied;

    public Tile(Tile n, Tile e, Tile s, Tile w){
        northTile = n;
        eastTile = e;
        southTile = s;
        westTile = w;
        occupied = false;
    }

    /**
     * boolean placeable (ArrayDeque directions)
     * checks if self is occupied
     *  - if not occupied, pops a direction from directions
     *      - calls placeable with popped directions on Tile lying towards direction
     *
     */

}
