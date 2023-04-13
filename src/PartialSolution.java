package org.example;

import java.util.ArrayList;

/**
 * PartialSolution represents a state of the game
 */
public class PartialSolution implements Comparable<PartialSolution>{
    Board copy;
    ArrayList<Move> moves;
    int cost;

    /**
     * Constructor
     */
    public PartialSolution(Board board) {
        copy = new Board(board);
        this.moves = new ArrayList<>();
        cost = copy.manhattan();
    }

    /**
     * Copy constructor
     *
     * @param that PartialSolution to be copied
     */
    public PartialSolution(PartialSolution that) {
        copy = new Board(that.copy);
        this.moves = new ArrayList<>(that.moves);
        cost = copy.manhattan() + moves.size();
    }

    /**
     * Performs a move on the board of the partial solution and updates the cost.
     *
     * @param move The move that is to be performed
     */
    public void doMove(Move move) {
        if(copy.checkMove(move)) {
            this.copy.doMove(move);
            this.moves.add(move);
        }
        this.cost = this.copy.manhattan() + this.moves.size();
    }

    /**
     * Tests whether the solution has been reached
     *
     * @return True, if the board is in goal state
     */
    public boolean isSolution() {
        return copy.isSolved();
    }

    /**
     * Return the sequence of moves which leads from the initial board to the current state.
     *
     * @return Move sequence leading to this state of solution
     */
    public ArrayList<Move> moveSequence() {
        return this.moves;
    }

    /**
     * Generates all possible moves on the current board, except the move which would undo the previous one
     *
     * @return Valid moves
     */
    public Iterable<Move> validMoves() {
        if(moves.size() != 0) {
            return copy.validMoves(moves.get(moves.size()-1));
        }

        return copy.validMoves(null);
    }

    /**
     * Compares partial solutions based on their cost.
     *
     * @param that the other partial solution
     * @return result of cost comparistion between this and that
     */
    public int compareTo(PartialSolution that) {
        return cost - that.cost;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder("Partial solution with moves: \n");
        for (Move move : moveSequence()) {
            msg.append(move).append(", ");
        }
        return msg.substring(0, msg.length() - 2);
    }


    public static void main(String[] args) {
        String filename = "src\\main\\java\\org\\example\\board.txt";
        Board board = new Board(filename);
        PartialSolution psol = new PartialSolution(board);
        psol.doMove(new Move(new Position(1, 2), 0));
        psol.doMove(new Move(new Position(2, 2), 3));
        AStarPuzzle.printBoardSequence(board, psol.moveSequence());
    }

}


