package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;

    private static final int[][] DIRECTIONS = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0},    // по прямой
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}   // по диагонали
    };

    private static final int STRAIGHT_COST = 10;
    private static final int DIAGONAL_COST = 14;

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {

        if (attackUnit == null || targetUnit == null || existingUnitList == null) {
            return Collections.emptyList();
        }

        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        int targetX = targetUnit.getxCoordinate();
        int targetY = targetUnit.getyCoordinate();

        if (!isValid(startX, startY) || !isValid(targetX, targetY)) {
            return Collections.emptyList();
        }

        if (startX == targetX && startY == targetY) {
            return Arrays.asList(new Edge(startX, startY));
        }

        Set<Point> obstacles = createObstacleSet(existingUnitList, startX, startY, targetX, targetY);

        if (obstacles.contains(new Point(targetX, targetY))) {
            return Collections.emptyList();
        }

        return bfsSearch(startX, startY, targetX, targetY, obstacles);
    }

    private Set<Point> createObstacleSet(List<Unit> units, int startX, int startY, int targetX, int targetY) {
        Set<Point> obstacles = new HashSet<>();

        for (Unit unit : units) {
            int x = unit.getxCoordinate();
            int y = unit.getyCoordinate();

            if (!(x == startX && y == startY) && !(x == targetX && y == targetY)) {
                obstacles.add(new Point(x, y));
            }
        }

        return obstacles;
    }

    private List<Edge> bfsSearch(int startX, int startY, int targetX, int targetY, Set<Point> obstacles) {
        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        Point[][] parent = new Point[WIDTH][HEIGHT];
        Queue<Point> queue = new LinkedList<>();

        Point start = new Point(startX, startY);
        queue.add(start);
        visited[startX][startY] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.x == targetX && current.y == targetY) {
                return reconstructPath(parent, current.x, current.y);
            }

            for (int[] dir : DIRECTIONS) {
                int neighborX = current.x + dir[0];
                int neighborY = current.y + dir[1];

                if (!isValid(neighborX, neighborY) ||
                        visited[neighborX][neighborY] ||
                        obstacles.contains(new Point(neighborX, neighborY))) {
                    continue;
                }

                visited[neighborX][neighborY] = true;
                parent[neighborX][neighborY] = current;
                queue.add(new Point(neighborX, neighborY));
            }
        }

        return Collections.emptyList();
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    private List<Edge> reconstructPath(Point[][] parent, int endX, int endY) {
        List<Edge> path = new ArrayList<>();
        Point current = new Point(endX, endY);

        while (current != null) {
            path.add(new Edge(current.x, current.y));
            current = parent[current.x][current.y];
        }

        Collections.reverse(path);
        return path;
    }

    private static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}