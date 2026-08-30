package example.DSAs_prac;

import java.util.*;

public class Robot_move_uber {

    static List<int[]> findRobots(char[][] grid, int[] query) {
        // YOU WRITE THIS
        int rows = grid.length;
        int cols = grid[0].length;

        List<int[]> result = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                if (grid[i][j] == 'O') {

                    boolean left = canMove(grid, i, j, query[0], 0, 0);
                    boolean top = canMove(grid, i, j, query[1], 1, 0);
                    boolean bottom = canMove(grid, i, j, query[2], 2, 0);
                    boolean right = canMove(grid, i, j, query[3], 3, 0);

                    if (left && top && bottom && right) {
                        result.add(new int[]{i, j});
                    }
                }
            }
        }

        return result;
    }

    private static boolean canMove(char[][] grid, int i, int j, int distance, int direction, int steps) {
        if (steps == distance) {
            return true;
        }
        int nextI = i;
        int nextJ = j;

        if (direction == 0) {
            nextJ--;           // left
        } else if (direction == 1) {
            nextI--;           // top
        } else if (direction == 2) {
            nextI++;           // bottom
        } else {
            nextJ++;           // right
        }

        if (nextI < 0 || nextI >= grid.length || nextJ < 0 || nextJ >= grid[0].length) {
            return false;
        }

        if (grid[nextI][nextJ] == 'X') {
            return false;
        }

        return canMove(grid, nextI, nextJ, distance, direction, steps + 1);
    }

    static void printResult(List<int[]> result) {
        System.out.print("[");
        for (int i = 0; i < result.size(); i++) {
            int[] pos = result.get(i);

            System.out.print("[" + pos[0] + ", " + pos[1] + "]");

            if (i < result.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    public static void main(String[] args) {

        // =========================
        // TEST CASE 1
        // =========================

        char[][] grid1 = {{'O', 'E', 'E', 'E', 'X'}, {'E', 'O', 'X', 'X', 'X'}, {'E', 'E', 'E', 'E', 'E'}, {'X', 'E', 'O', 'E', 'E'}, {'X', 'E', 'X', 'E', 'X'}};

        int[] query1 = {2, 2, 4, 1};

        System.out.println("Test Case 1:");
        printResult(findRobots(grid1, query1));


        // =========================
        // TEST CASE 2
        // =========================

        char[][] grid2 = {{'X', 'E', 'E', 'E', 'X'}, {'E', 'E', 'O', 'E', 'E'}, {'E', 'E', 'E', 'E', 'E'}, {'E', 'O', 'E', 'E', 'E'}, {'X', 'E', 'E', 'E', 'X'}};

        int[] query2 = {1, 1, 2, 1};

        System.out.println("\nTest Case 2:");
        printResult(findRobots(grid2, query2));


        // =========================
        // TEST CASE 3
        // =========================

        char[][] grid3 = {{'E', 'E', 'E', 'X', 'E', 'E'}, {'E', 'O', 'E', 'E', 'E', 'X'}, {'E', 'E', 'E', 'E', 'E', 'E'}, {'X', 'E', 'E', 'O', 'E', 'E'}, {'E', 'E', 'E', 'E', 'E', 'E'}, {'E', 'X', 'E', 'E', 'E', 'E'}};

        int[] query3 = {2, 1, 2, 2};

        System.out.println("\nTest Case 3:");
        printResult(findRobots(grid3, query3));


        // =========================
        // TEST CASE 4
        // Multiple valid robots
        // =========================

        char[][] grid4 = {{'X', 'X', 'X', 'X', 'X', 'X', 'X'}, {'X', 'E', 'O', 'E', 'E', 'E', 'X'}, {'X', 'E', 'E', 'E', 'E', 'E', 'X'}, {'X', 'E', 'O', 'E', 'O', 'E', 'X'}, {'X', 'E', 'E', 'E', 'E', 'E', 'X'}, {'X', 'X', 'X', 'X', 'X', 'X', 'X'}};

        int[] query4 = {1, 1, 1, 1};

        System.out.println("\nTest Case 4:");
        printResult(findRobots(grid4, query4));


        // =========================
        // TEST CASE 5
        // Boundary as blocker
        // =========================

        char[][] grid5 = {{'O', 'E', 'O'}, {'E', 'E', 'E'}, {'O', 'E', 'O'}};

        int[] query5 = {1, 1, 1, 1};

        System.out.println("\nTest Case 5:");
        printResult(findRobots(grid5, query5));


        // =========================
        // TEST CASE 6
        // No robot satisfies
        // =========================

        char[][] grid6 = {{'O', 'E', 'E', 'X'}, {'E', 'E', 'E', 'E'}, {'X', 'E', 'O', 'E'}, {'E', 'E', 'E', 'X'}};

        int[] query6 = {3, 3, 3, 3};

        System.out.println("\nTest Case 6:");
        printResult(findRobots(grid6, query6));


        // =========================
        // TEST CASE 7
        // One robot
        // =========================

        char[][] grid7 = {{'E', 'E', 'E', 'E', 'E'}, {'E', 'E', 'O', 'E', 'E'}, {'E', 'E', 'E', 'E', 'E'}, {'E', 'E', 'E', 'E', 'E'}, {'E', 'E', 'E', 'E', 'E'}};

        int[] query7 = {2, 1, 2, 1};

        System.out.println("\nTest Case 7:");
        printResult(findRobots(grid7, query7));
    }
}