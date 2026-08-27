package example.DSAs_prac;

public class  cityWithCitycenterkotak {

    boolean hasCenter;
    public int findCityCenter(char[][] grid) {
        int n = grid.length;
        int m = grid[0].length;
        boolean[][] vis = new boolean[n][m];

        int count = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == 'C' && !vis[i][j]) {

                    hasCenter = false;   // reset for each city
                    dfs(grid, i, j, vis, n, m);

                    if (hasCenter) count++;
                }
            }
        }
        return count;
    }

    void dfs(char[][] grid, int i, int j, boolean[][] vis, int n, int m) {
        if (i < 0 || j < 0 || i >= n || j >= m ||
                vis[i][j] || grid[i][j] != 'C') return;

        vis[i][j] = true;

        if (isCenter(grid, i, j, n, m)) {
            hasCenter = true;
        }

        dfs(grid, i + 1, j, vis, n, m);
        dfs(grid, i - 1, j, vis, n, m);
        dfs(grid, i, j + 1, vis, n, m);
        dfs(grid, i, j - 1, vis, n, m);
    }

    boolean isCenter(char[][] grid, int i, int j, int n, int m) {
        return i > 0 && i < n - 1 && j > 0 && j < m - 1 &&
                grid[i - 1][j] == 'C' &&
                grid[i + 1][j] == 'C' &&
                grid[i][j - 1] == 'C' &&
                grid[i][j + 1] == 'C';
    }
}