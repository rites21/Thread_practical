package example.DSAs_prac;

import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class Uber_SDE2_25aug {

    public static void main(String[] args) {

        // Test 1
        char[][] grid1 = {{'S', '.', '.', 'C'}, {'#', '#', '.', '#'}, {'.', '.', '.', 'T'}};

        System.out.println(Arrays.toString(findOptimalPath(grid1, 10)));
        // Expected: [0, 6, 6]


        // Test 2
        char[][] grid2 = {{'S', '.', 'C', '.', 'T'}, {'.', '.', '#', '.', '.'}, {'.', '.', '.', '.', '.'}};

        System.out.println(Arrays.toString(findOptimalPath(grid2, 10)));


        // Test 3
        char[][] grid3 = {{'S', '.', '.', '.', 'T'}, {'#', '#', '#', '#', '.'}, {'C', '.', '.', '.', '.'}};

        System.out.println(Arrays.toString(findOptimalPath(grid3, 3)));


        // Test 4
        char[][] grid4 = {{'S', '.', '.', '#', 'T'}, {'#', '#', '.', '#', '#'}, {'C', '.', '.', '.', '.'}};

        System.out.println(Arrays.toString(findOptimalPath(grid4, 4)));


        // Test 5
        char[][] grid5 = {{'S', 'C', '.', '.', 'T'}, {'.', '#', '#', '#', '.'}, {'.', 'C', '.', '.', '.'}, {'.', '.', '.', 'C', '.'}};

        System.out.println(Arrays.toString(findOptimalPath(grid5, 10)));


        // Test 6
        char[][] grid6 = {{'S', '#', '#'}, {'.', '#', 'T'}, {'.', '.', '.'}};

        System.out.println(Arrays.toString(findOptimalPath(grid6, 10)));
        // Expected: [-1, -1, -1]
    }


    public static int[] findOptimalPath(char[][] grid, int battery) {

        int m = grid.length;
        int n = grid[0].length;

        int startRow = -1;
        int startCol = -1;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 'S') {
                    startRow = i;
                    startCol = j;
                }
            }
        }

        PriorityQueue<State> pq = new PriorityQueue<>((a, b) -> {
            if (a.chargingCells != b.chargingCells) {
                return Integer.compare(a.chargingCells, b.chargingCells);
            }

            if (a.batteryRequired != b.batteryRequired) {
                return Integer.compare(a.batteryRequired, b.batteryRequired);
            }
            return Integer.compare(a.moves, b.moves);
        });

        Set<String> set = new HashSet<>();
        pq.offer(new State(startRow, startCol, battery, 0, 0, 0));

        int[][] dir = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};

        while (!pq.isEmpty()) {
            State curr = pq.poll();
            String currentKey = curr.row + "," + curr.col + "," + curr.batteryLeft + "," + curr.chargingCells + "," + curr.batteryRequired;

            if (set.contains(currentKey)) {
                continue;
            }

            set.add(currentKey);

            if (grid[curr.row][curr.col] == 'T') {
                return new int[]{curr.chargingCells, curr.batteryRequired, curr.moves};
            }
            for (int[] d : dir) {
                int nc = curr.row + d[0];
                int nr = curr.col + d[1];

                // Boundary
                if (nr < 0 || nr >= m || nc < 0 || nc >= n) {
                    continue;
                }

                // Blocked cell
                if (grid[nr][nc] == '#') {
                    continue;
                }

                // Cannot move without battery
                if (curr.batteryLeft == 0) {
                    continue;
                }

                int newBatteryLeft = curr.batteryLeft -1 ;
                int newMoves = curr.moves + 1;
                int newChargingCells = curr.chargingCells;
                int newBatteryRequired = curr.batteryRequired;

                /*
                 * Battery used since the last recharge.
                 *
                 * batteryCapacity - batteryLeft
                 */

                int batteryUsed = battery - newBatteryLeft;

                newBatteryRequired = Math.max(newBatteryRequired, batteryUsed);

                /*
                 * Entering C:
                 * count charging cell
                 * then recharge
                 */

                if (grid[nr][nc] == 'C') {

                    newChargingCells++;

                    newBatteryLeft = battery;
                }

                pq.offer(new State(nr, nc, newBatteryLeft, newChargingCells, newBatteryRequired, newMoves));

            }
        }
        return null;
    }

    static class State {
        int row;
        int col;
        int batteryLeft;

        int chargingCells;
        int batteryRequired;
        int moves;

        public State(int row, int col, int batteryLeft, int chargingCells, int batteryRequired, int moves) {
            this.row = row;
            this.col = col;
            this.batteryLeft = batteryLeft;
            this.chargingCells = chargingCells;
            this.batteryRequired = batteryRequired;
            this.moves = moves;
        }
    }
}
