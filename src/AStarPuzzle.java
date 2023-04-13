
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import java.util.PriorityQueue;

public class AStarPuzzle {

    /**
     * Finds the shortest solution to a given sliding puzzle using the A* algorithm
     */
    public static PartialSolution solveByAStar(Board board) {

        PriorityQueue<PartialSolution> pq = new PriorityQueue<>();
        PartialSolution part = new PartialSolution(board);
        pq.add(part);

        while (!pq.isEmpty()) {
            PartialSolution min = pq.poll();

            if (min.isSolution()) { return min; }

            for (Move m: min.validMoves()) {
                PartialSolution temp = new PartialSolution(min);
                temp.doMove(m);
                pq.add(temp);
            }
        }
        return null;
    }


    public static void printBoardSequence(Board board, Iterable<Move> moveSequence) {
        int moveno = 0;
        for (Move move : moveSequence) {
            System.out.println("Manhattan metric: " + board.manhattan() + " -> cost = " + (moveno + board.manhattan()));
            System.out.println(board);
            System.out.println((++moveno) + ". Move: " + move);
            board.doMove(move);
        }
        System.out.println("Solved board:");
        System.out.println(board);
    }

    public static void main(String[] args) {

        String filename = "src\\board2.txt";

        Board boardPrint = new Board(filename);
        Board boardDisplay = new Board(filename);

        long start = System.nanoTime();
        PartialSolution sol = solveByAStar(boardPrint);
        long duration1 = (System.nanoTime() - start) / 1000;
        System.out.println("Time: " + duration1 / 1000 + " ms");
        if (sol == null) {
            System.out.println("No solution found.");
        } else {
            printBoardSequence(boardPrint, sol.moveSequence());

            JFrame frame = new JFrame("Grid");
            frame.setSize(800, 600);
            frame.setPreferredSize(frame.getSize());
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            frame.add(new Display(boardDisplay, sol.moveSequence()));

            frame.pack();
            frame.setVisible(true);

        }
    }

}
