package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;


public class PuzzleBoard {

    private int steps;
    private PuzzleBoard previousBoard;
    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;

    /*
    √ Implement the constructor for PuzzleBoard.
    √ It should take the passed-in Bitmap object and divide it into NUM_TILES x NUM_TILES equal-sized pieces.
        (Hint: You can use the Bitmap.createBitmap and Bitmap.createScaledBitmap methods to do so.)
    √ Then use each "chunk" of the bitmap to initialize a tile object.
    √ Remember to leave the last tile null to represent the 'empty' tile!
     */
    PuzzleBoard(Bitmap bitmap, int parentWidth) {

        Bitmap source = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, true);

        steps = 0;
        previousBoard = null;
        tiles = new ArrayList<>();

        int x;
        int y;
        int width = source.getWidth() / 3;

        for (int i = 0; i < 8; i++) {
            x = (i % 3) * width;
            y = (i / 3) * width;

            Bitmap temp = Bitmap.createBitmap(source, x, y, width, width);
            tiles.add(new PuzzleTile(temp, i));
        }
        tiles.add(null);
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps = otherBoard.getSteps() + 1;
        previousBoard = otherBoard;
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int x = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (x >= 0 && x < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(x, nullY)) == null) {
                swapTiles(XYtoIndex(x, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    /*
    √ Implement PuzzleBoard.neighbours which is a method that returns an ArrayList of all the PuzzleBoard configurations that are possible by moving one tile in the current PuzzleBoard.
        Note that depending on where the empty square is, there could be 2, 3 or 4 possible moves.
    So implement the neighbours method to:
        √ locate the empty square in the current board
        √ consider all the neighbours of the empty square (using the NEIGHBOUR_COORDS array)
        √ if the neighbouring square is valid (within the boundaries of the puzzle), make a copy of the current board (using the provided copy constructor),
            move the tile in that square to the empty square and add this copy of the board to the list of neighbours to be returned

     */
    public ArrayList<PuzzleBoard> neighbours() {

        ArrayList<PuzzleBoard> validMoves = new ArrayList<>();

        int x = 0;
        int y = 0;
        int index = 0;

        // locate the empty square in the current board
        for (; index < tiles.size(); index++) {
            if (tiles.get(index) == null) {
                x = index % 3;
                y = index / 3;
                break;
            }
        }

        // check top neighbor
        if (y + NEIGHBOUR_COORDS[2][1] > - 1) { // NEIGHBOUR_COORDS[2][1] = -1
            PuzzleBoard copy = new PuzzleBoard(this);
            // swap with the tile above it
            copy.swapTiles(index, index - 3);
            validMoves.add(copy);
        }

        // check bottom neighbor
        if (y + NEIGHBOUR_COORDS[3][1] < 3) { // NEIGHBOUR_COORDS[3][1] = 1
            PuzzleBoard copy = new PuzzleBoard(this);
            // swap with the tile below it
            copy.swapTiles(index, index + 3);
            validMoves.add(copy);
        }

        // check left neighbor
        if (x + NEIGHBOUR_COORDS[0][0] > - 1) { // NEIGHBOUR_COORDS[0][0] = -1
            PuzzleBoard copy = new PuzzleBoard(this);
            // swap with the tile to the left of it
            copy.swapTiles(index, index - 1);
            validMoves.add(copy);
        }

        // check right neighbor
        if (x + NEIGHBOUR_COORDS[1][0] < 3) { // NEIGHBOUR_COORDS[1][0] = 1
            PuzzleBoard copy = new PuzzleBoard(this);
            // swap with the tile to the right of it
            copy.swapTiles(index, index + 1);
            validMoves.add(copy);
        }

        return validMoves;
    }

    public int priority() {

        return 0;
    }

    public int getSteps() {
        return steps;
    }
    public void setSteps(int steps) {
        this.steps = steps;
    }

    public PuzzleBoard getPreviousBoard() {
        return previousBoard;
    }

}
