
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Stack;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Board {
    private int[][] field;
    private int nx;
    private int ny;
    private Position blank;
    private static Stack<Position> positions = null;
    private static int posnx;
    private static int posny;

    private final static int OFF = -1;   // used for positions that are outside of the actual field
    private final static int BLANK = 0;  // denotes the field that is blank

    /**
     * Constructor generates an empty board of the given dimensions
     */
    public Board(int nx, int ny) {
        this.nx = nx;
        this.ny = ny;
        constructorBody();
    }

    /**
     * Constructor -> reads a Board from a text file
     */
    public Board(String filename) {
        Scanner in = null;
        try {

            in = new Scanner(new FileInputStream(filename));
        } catch (IOException e) {
            System.err.println("!!!!! Cannot read file '" + filename + "'.");
        }

        nx = in.nextInt();
        ny = in.nextInt();
        System.out.println(nx);
        System.out.println(ny);
        constructorBody();

        for (int y = 0; y < ny; y++) {
            for (int x = 0; x < nx; x++) {
                field[y][x] = in.nextInt();
                if (field[y][x] == BLANK)
                    blank = new Position(x, y);
            }
        }
        in.close();
    }

    private void constructorBody() {
        field = new int[ny][nx];

        if (positions == null || posnx != nx || posny != ny) {
            positions = new Stack<>();
            for (int y = 0; y < ny; y++)
                for (int x = 0; x < nx; x++)
                    positions.push(new Position(x, y));
            posnx = nx;
            posny = ny;
        }
    }

    public int getWidth() { return nx; }
    public int getHeight() { return ny; }

    /**
     * Copy constructor
     */
    public Board(Board that) {
        this(that.nx, that.ny);
        for (int y = 0; y < ny; y++) {
            this.field[y] = that.field[y].clone();
        }
        this.blank = that.blank;
    }

    /**
     * Return the value of a field
     */
    public int getField(Position pos) {
        if (pos.x < 0 || pos.x >= nx || pos.y < 0 || pos.y >= ny)
            return OFF;
        else
            return field[pos.y][pos.x];
    }

    /**
     * Put a value on the field at the specified position of the board.
     */
    public void setField(Position pos, int token) throws InputMismatchException {
        if (getField(pos) == OFF || token < 0 || token >= nx * ny)
            throw new InputMismatchException();
        field[pos.y][pos.x] = token;
    }

    /**
     * Check whether a move is valid for the current board.
     */
    public boolean checkMove(Move move) {
        return getField(move.targetPosition(0)) > BLANK &&
                getField(move.targetPosition(1)) == BLANK;
    }

    /**
     * Do the specified move
     */
    public void doMove(Move move) {
        blank = move.targetPosition(0);
        int token = getField(blank);
        setField(move.targetPosition(1), token);
        setField(blank, BLANK);
    }

    /**
     * Returns all possible moves for the current board
     */
    public Iterable<Move> validMoves() {
        return validMoves(null);
    }

    /**
     * Returns all possible moves for the current board excluding the move that would undo the last one
     */
    public Iterable<Move> validMoves(Move lastMove) {
        Stack<Move> moves = new Stack<>();
        for (int dir = 0; dir < 4; dir++) {
            Move invmove = new Move(blank, dir);
            Position source = invmove.targetPosition(-1);
            Move move = new Move(source, dir);
            if (checkMove(move) && !move.isInverse(lastMove))
                moves.push(move);
        }
        return moves;
    }

    /**
     * Checks whether the state of the board is the solved state
     */
    public boolean isSolved() {
        for (int counter = 0; counter < nx * ny - 1; counter++) {
            if (getField(new Position(counter % nx, counter / nx)) != counter + 1) return false;
        }
        return true;
    }

    /**
     * Computes the Manhattan distance between the current board state and the solved state
     */
    public int manhattan() {

        int res = 0;
        int temp;
        int[][] indexMatrix = new int[ny][nx];

        for (int y = 0; y < ny; y++) {
            for (int x = 0; x < nx; x++) {

                int index = x + y*nx + 1;
                indexMatrix[y][x] = index;

            }
        }

        for (int y = 0; y < ny; y++) {
            for (int x = 0; x < nx; x++) {

                if(field[y][x] != indexMatrix[y][x] && field[y][x] != 0){
                    temp = field[y][x];

                    for (int i = 0; i < ny; i++) {
                        for (int j = 0; j < nx; j++) {

                            if(indexMatrix[i][j] == temp){
                                res += Math.abs(i-y) + Math.abs(j-x);
                            }

                        }
                    }
                }
            }
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Position pos : positions) {
            str.append(String.format("%2d ", getField(pos)));
            if (pos.x == nx - 1)
                str.append("\n");
        }
        return str.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board that = (Board) o;
        for (Position pos : positions) {
            if (getField(pos) != that.getField(pos))
                return false;
        }
        return true;
    }


    public static void main(String[] args) {
        String filename = "src\\board.txt";
        Board board = new Board(filename);
        System.out.println(board + "Manhattan: " + board.manhattan());
    }
}

