import edu.princeton.cs.algs4.WeightedQuickUnionUF;


public class Percolation {
    /** Size of the grid (N-by-N). */
    private final int n;
    /** open[r][c] is true if the site at (row r, col c) is open.
     *  (0, 0) is the upper-left site. */
    private final boolean[][] open;
    /** Number of open sites so far. */
    private int openCount;
    /** Union-find over all N*N sites plus two virtual sites:
     *  top at index n*n, bottom at index n*n + 1. */
    private final WeightedQuickUnionUF uf;
    private final int topIndex;
    private final int bottomIndex;

    /** Creates an N-by-N grid, with all sites initially blocked.
     *  @throws IllegalArgumentException if N <= 0. */
    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("Grid size must be positive.");
        }
        this.n = N;
        this.open = new boolean[N][N];
        this.openCount = 0;
        this.topIndex = N * N;
        this.bottomIndex = N * N + 1;
        this.uf = new WeightedQuickUnionUF(N * N + 2);
    }

    /** Opens the site (row, col) if it is not open already.
     *  @throws IndexOutOfBoundsException if row or col is outside [0, N). */
    public void open(int row, int col) {
        checkBounds(row, col);
        if (open[row][col]) {
            return;
        }
        open[row][col] = true;
        openCount++;

        int idx = siteIndex(row, col);
        // Union with any open neighboring sites (left, right, up, down).
        unionWithNeighbor(row, col, row, col - 1);
        unionWithNeighbor(row, col, row, col + 1);
        unionWithNeighbor(row, col, row - 1, col);
        unionWithNeighbor(row, col, row + 1, col);

        // Connect to the virtual sites.
        if (row == 0) {
            uf.union(idx, topIndex);
        }
        if (row == n - 1) {
            uf.union(idx, bottomIndex);
        }
    }

    /** If (nRow, nCol) is in bounds and open, unions it with the site at
     *  (row, col). */
    private void unionWithNeighbor(int row, int col, int nRow, int nCol) {
        if (nRow >= 0 && nRow < n && nCol >= 0 && nCol < n && open[nRow][nCol]) {
            uf.union(siteIndex(row, col), siteIndex(nRow, nCol));
        }
    }

    /** Is the site (row, col) open?
     *  @throws IndexOutOfBoundsException if row or col is outside [0, N). */
    public boolean isOpen(int row, int col) {
        checkBounds(row, col);
        return open[row][col];
    }

    /** Is the site (row, col) full?
     *  @throws IndexOutOfBoundsException if row or col is outside [0, N). */
    public boolean isFull(int row, int col) {
        checkBounds(row, col);
        return open[row][col] && uf.connected(siteIndex(row, col), topIndex);
    }

    /** Number of open sites. Runs in constant time. */
    public int numberOfOpenSites() {
        return openCount;
    }

    /** Does the system percolate? */
    public boolean percolates() {
        return uf.connected(topIndex, bottomIndex);
    }

    /** Maps a (row, col) pair to its union-find index. */
    private int siteIndex(int row, int col) {
        return row * n + col;
    }

    /** Throws if (row, col) is outside the grid. */
    private void checkBounds(int row, int col) {
        if (row < 0 || row >= n || col < 0 || col >= n) {
            throw new IndexOutOfBoundsException("Site (" + row + ", " + col + ") is outside the grid.");
        }
    }

}
