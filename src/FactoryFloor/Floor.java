package FactoryFloor;

import Machines.*;
import Utils.Orientation;

import java.util.*;

import static java.util.Comparator.*;

public class Floor implements Machines {
    private Tile[][] tiles;
    private ArrayList<Machine> machines;

    public Floor(int floorwidth, int floorlength){
        tiles = new Tile[floorlength][floorwidth];
        machines = new ArrayList<>();
        generateTiles();
    }

    private void generateTiles(){
        for(int j = 0; j < tiles.length; ++j){
            for (int i = 0; i < tiles[0].length; ++i){
                tiles[j][i] = new Tile(i, j);
            }
        }
        assignNeighbors();
    }

    private void assignNeighbors(){
        for(int i = 0; i < tiles.length; ++i){
            for(int j = 0; j < tiles[0].length; ++j){
                if (i == 0){
                    if (j == 0){
                        tiles[i][j].setTile(Orientation.E, tiles[i][j + 1]);
                        tiles[i][j].setTile(Orientation.S, tiles[i + 1][j]);
                    } else if (j == tiles[0].length - 1){
                        tiles[i][j].setTile(Orientation.W, tiles[i][j - 1]);
                        tiles[i][j].setTile(Orientation.S, tiles[i + 1][j]);
                    } else {
                        tiles[i][j].setTile(Orientation.E, tiles[i][j + 1]);
                        tiles[i][j].setTile(Orientation.W, tiles[i][j - 1]);
                        tiles[i][j].setTile(Orientation.S, tiles[i + 1][j]);
                    }
                } else if (i == tiles.length - 1){
                    if (j == 0){
                        tiles[i][j].setTile(Orientation.E, tiles[i][j + 1]);
                        tiles[i][j].setTile(Orientation.N, tiles[i - 1][j]);
                    } else if (j == tiles[0].length - 1){
                        tiles[i][j].setTile(Orientation.W, tiles[i][j - 1]);
                        tiles[i][j].setTile(Orientation.N, tiles[i - 1][j]);
                    } else {
                        tiles[i][j].setTile(Orientation.E, tiles[i][j + 1]);
                        tiles[i][j].setTile(Orientation.W, tiles[i][j - 1]);
                        tiles[i][j].setTile(Orientation.N, tiles[i - 1][j]);
                    }
                } else {
                    if (j == 0){
                        tiles[i][j].setTile(Orientation.N, tiles[i - 1][j]);
                        tiles[i][j].setTile(Orientation.E, tiles[i][j + 1]);
                        tiles[i][j].setTile(Orientation.S, tiles[i + 1][j]);
                    } else if (j == tiles[0].length - 1){
                        tiles[i][j].setTile(Orientation.N, tiles[i - 1][j]);
                        tiles[i][j].setTile(Orientation.S, tiles[i + 1][j]);
                        tiles[i][j].setTile(Orientation.W, tiles[i][j - 1]);
                    } else {
                        tiles[i][j].setTile(Orientation.N, tiles[i - 1][j]);
                        tiles[i][j].setTile(Orientation.E, tiles[i][j + 1]);
                        tiles[i][j].setTile(Orientation.S, tiles[i + 1][j]);
                        tiles[i][j].setTile(Orientation.W, tiles[i][j - 1]);
                    }
                }
            }
        }
    }

    public boolean place(Shapes shape, int x, int y){
        ArrayDeque<Orientation> orientationArrayDeque = Machines.getOrientationArrayDeque(shape);
        for(int j = new Random().nextInt(3); j < 3; ++j){
            orientationArrayDeque = Machines.rotateOrientations(orientationArrayDeque, Rotations.CW);
        }
        int i = 0;
        boolean placeable = false;
        while (i < 4 && !(placeable = tiles[x][y].placeable(orientationArrayDeque.clone()))){
            ++i;
            for(int j = new Random().nextInt(3); j < 3; ++j){
                orientationArrayDeque = Machines.rotateOrientations(orientationArrayDeque, Rotations.CW);
            }
        }
        ArrayList<Tile> addedShape = null;
        if (placeable){
            addedShape = tiles[x][y].place(orientationArrayDeque.clone(), shape);
            machines.add(new Machine(addedShape, shape));
        }
        return placeable;
    }

    public ArrayList<Machine> getMachines() {
        machines.sort(naturalOrder());
        return machines;
    }

    public void display(){
        for (Tile[] tileArr : tiles){
            for (Tile tile : tileArr){
                char x = (tile.isOccupied())? Machines.getDisplayChar(tile.getShape()) : ' ';
                System.out.print(x);
            }
            System.out.println();
        }
    }

}
