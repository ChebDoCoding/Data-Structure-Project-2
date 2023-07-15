
package Project2;

import de.zabuza.maglev.external.algorithms.Path;
import de.zabuza.maglev.external.algorithms.ShortestPathComputationBuilder;
import de.zabuza.maglev.external.graph.Graph;
import de.zabuza.maglev.external.graph.simple.SimpleEdge;
import de.zabuza.maglev.external.graph.simple.SimpleGraph;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.StringJoiner;

public class Project2V2 {
    public static void main(String[] args) {
        Graph<GameState, SimpleEdge<GameState>> graph = generateGraph();

        var algo = new ShortestPathComputationBuilder<>(graph).resetOrdinaryDijkstra()
                .build();

        GameState solvedState =
                new GameState(new boolean[][] { { true, true, true }, { true, true, true }, { true, true, true } });
        var pathTree = algo.shortestPathReachable(solvedState);

        var longestShortestPath = pathTree.getLeaves()
                .stream()
                .map(pathTree::getPathTo)
                .map(Optional::orElseThrow)
                .max(Comparator.comparing(Path::getTotalCost))
                .orElseThrow();

        System.out.println("The longest shortest path has cost: " + longestShortestPath.getTotalCost());
        System.out.println("The states are:");
        System.out.println(longestShortestPath.iterator().next().getEdge().getSource());
        for (var edgeCost : longestShortestPath) {
            System.out.println("------------");
            System.out.println(edgeCost.getEdge().getDestination());
        }
    }

    private static Graph<GameState, SimpleEdge<GameState>> generateGraph() {
        SimpleGraph<GameState, SimpleEdge<GameState>> graph = new SimpleGraph<>();
        generateNodes(graph);
        generateEdges(graph);
        return graph;
    }

    private static void generateNodes(Graph<GameState, SimpleEdge<GameState>> graph) {
        for (int i = 0; i < 1 << 9; i++) {
            String boardString = String.format("%09d", Integer.parseInt(Integer.toBinaryString(i)));
            graph.addNode(GameState.of(boardString, 3, 3));
        }
    }

    private static void generateEdges(Graph<GameState, SimpleEdge<GameState>> graph) {
        for (GameState source : graph.getNodes()) {
            // Click on each field
            boolean[][] board = source.getBoard();
            for (int x = 0; x < board.length; x++) {
                for (int y = 0; y < board[x].length; y++) {
                    GameState destination = new GameState(board);
                    destination.click(x, y);

                    graph.addEdge(new SimpleEdge<>(source, destination, 1));
                }
            }
        }
    }

    private static class GameState {

        public static GameState of(String boardString, int rows, int columns) {
            boolean[][] board = new boolean[rows][columns];
            int i = 0;
            for (int x = 0; x < rows; x++) {
                for (int y = 0; y < columns; y++) {
                    board[x][y] = boardString.charAt(i) == '1';
                    i++;
                }
            }
            return new GameState(board);
        }

        private final boolean[][] board;

        private GameState(boolean[][] board) {
            this.board = new boolean[board.length][];
            for (int x = 0; x < board.length; x++) {
                this.board[x] = new boolean[board[x].length];
                for (int y = 0; y < board[x].length; y++) {
                    this.board[x][y] = board[x][y];
                }
            }
        }

        public boolean[][] getBoard() {
            return board;
        }

        @Override
        public String toString() {
            StringJoiner rowJoiner = new StringJoiner("\n");
            for (int x = 0; x < board.length; x++) {
                StringJoiner row = new StringJoiner(" ");
                for (int y = 0; y < board[x].length; y++) {
                    row.add(board[x][y] ? "1" : "0");
                }
                rowJoiner.add(row.toString());
            }
            return rowJoiner.toString();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final GameState gameState = (GameState) o;
            return Arrays.deepEquals(board, gameState.board);
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(board);
        }

        private void click(int x, int y) {
            toggle(x, y);

            toggle(x, y - 1);
            toggle(x, y + 1);

            toggle(x - 1, y);
            toggle(x + 1, y);
        }

        private void toggle(int x, int y) {
            if (x < 0 || y < 0 || x >= board.length || y >= board[x].length) {
                return;
            }

            board[x][y] = !board[x][y];
        }
    }
}
