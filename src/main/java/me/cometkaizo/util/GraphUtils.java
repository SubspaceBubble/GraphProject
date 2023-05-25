package me.cometkaizo.util;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static java.lang.Math.*;

public class GraphUtils {

    public static Point toCoordinate(Point point, int originX, int originY) {
        return new Point(toCoordinateX(point.x, originX), toCoordinateY(point.y, originY));
    }

    public static int toCoordinateX(int x, int originX) {
        return x - originX;
    }

    public static int toCoordinateY(int y, int originY) {
        return -(y - originY);
    }

    public static Point toScreen(Point point, int originX, int originY) {
        return new Point(toScreenX(point.x, originX), toScreenY(point.y, originY));
    }

    public static int toScreenX(int x, int originX) {
        return x + originX;
    }

    public static int toScreenY(int y, int originY) {
        return (-y) + originY;
    }

    public static double getAngleDegrees(Point2D origin, Point2D target) {
        return getAngleDegrees(origin.getX(), origin.getY(), target.getX(), target.getY());
    }

    public static double getAngleDegrees(double originX, double originY, double targetX, double targetY) {
        float angle = (float) toDegrees(atan2(targetY - originY, targetX - originX));
        return angle < 0 ? angle + 360 : angle;
    }

    public static double getAngleRadians(Point2D origin, Point2D target) {
        return getAngleRadians(origin.getX(), origin.getY(), target.getX(), target.getY());
    }

    public static double getAngleRadians(double originX, double originY, double targetX, double targetY) {
        float angle = (float) atan2(targetY - originY, targetX - originX);
        return angle < 0 ? angle + 2 * PI : angle;
    }

    public static Point toCartesian(double radius, double theta) {
        return new Point((int) (radius * cos(theta)), (int) (radius * sin(theta)));
    }

    public static double toCartesianX(double radius, double theta) {
        return radius * cos(theta);
    }

    public static double toCartesianY(double radius, double theta) {
        return radius * sin(theta);
    }

    public static double toPolarR(double x, double y) {
        return sqrt(x * x + y * y);
    }

    public static double toPolarT(double x, double y) {
        return atan2(x, y);
    }


    public static Point2D getClosestPointOnSegment(Line2D line, Point2D point) {
        return getClosestPointOnSegment(line.getX1(), line.getY1(), line.getX2(), line.getY2(), point.getX(), point.getY());
    }

    public static Point2D getClosestPointOnSegment(Line2D line, double px, double py) {
        return getClosestPointOnSegment(line.getX1(), line.getY1(), line.getX2(), line.getY2(), px, py);
    }

    public static Point2D getClosestPointOnSegment(double sx1, double sy1, double sx2, double sy2, double px, double py) {
        double xDelta = sx2 - sx1;
        double yDelta = sy2 - sy1;

        if ((xDelta == 0) && (yDelta == 0)) {
            return new Point2D.Double(sx1, sy1);
        }

        double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        if (u < 0) {
            return new Point2D.Double(sx1, sy1);
        } else if (u > 1) {
            return new Point2D.Double(sx2, sy2);
        } else {
            return new Point2D.Double(sx1 + u * xDelta, sy1 + u * yDelta);
        }
    }


    public static Point2D getClosestPointOnLine(Line2D line, Point2D point) {
        return getClosestPointOnLine(line.getX1(), line.getY1(), line.getX2(), line.getY2(), point.getX(), point.getY());
    }

    public static Point2D getClosestPointOnLine(Line2D line, double px, double py) {
        return getClosestPointOnLine(line.getX1(), line.getY1(), line.getX2(), line.getY2(), px, py);
    }

    public static Point2D getClosestPointOnLine(double sx1, double sy1, double sx2, double sy2, double px, double py) {
        double xDelta = sx2 - sx1;
        double yDelta = sy2 - sy1;

        if ((xDelta == 0) && (yDelta == 0)) {
            return new Point2D.Double(sx1, sy1);
        }

        double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        return new Point2D.Double(sx1 + u * xDelta, sy1 + u * yDelta);
    }


    public static boolean areInSameQuadrant(Point2D point1, Point2D point2, Point2D origin) {
        int xXor = (int)(point1.getX() - origin.getX()) ^ (int)(point2.getX() - origin.getX());
        int yXor = (int)(point1.getY() - origin.getY()) ^ (int)(point2.getY() - origin.getY());

        return xXor >= 0 && yXor >= 0;
    }

    /**
     * Returns if the first point is father from the origin than the second point is when they are in the same quadrant
     * @param point1 the first point
     * @param point2 the second point
     * @param origin the origin
     * @return whether the first point is farther
     */
    public static boolean isFartherFromInSameQuadrant(Point2D point1, Point2D point2, Point2D origin) {
        return areInSameQuadrant(point1, point2, origin) &&
                origin.distanceSq(point1) > origin.distanceSq(point2);
    }

    public static void forEachPoint(PathIterator pathIterator, BiConsumer<Float, Float> task) {
        float[] coords = new float[6];
        while (!pathIterator.isDone()) {
            switch (pathIterator.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO -> task.accept(coords[0], coords[1]);
                case PathIterator.SEG_QUADTO -> task.accept(coords[2], coords[3]);
                case PathIterator.SEG_CUBICTO -> task.accept(coords[4], coords[5]);
            }
            pathIterator.next();
        }
    }

    public static void forEachSegment(PathIterator pathIterator, BiConsumer<Point, Point> task) {
        float[] coords = new float[6];
        Point prevPoint = null;
        while (!pathIterator.isDone()) {
            Point point = null;
            switch (pathIterator.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO -> prevPoint = new Point((int) coords[0], (int) coords[1]);
                case PathIterator.SEG_LINETO -> point = new Point((int) coords[0], (int) coords[1]);
                case PathIterator.SEG_QUADTO -> point = new Point((int) coords[2], (int) coords[3]);
                case PathIterator.SEG_CUBICTO -> point = new Point((int) coords[4], (int) coords[5]);
            }
            if (point != null) {
                task.accept(prevPoint, point);
                prevPoint = point;
            }
            pathIterator.next();
        }
    }

    public static void forEachCubicArc(PathIterator pathIterator, BiConsumer<Point, float[]> task) {
        float[] coords = new float[6];
        Point prevPoint = null;
        while (!pathIterator.isDone()) {
            Point point = null;
            switch (pathIterator.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO -> prevPoint = new Point((int) coords[0], (int) coords[1]);
                case PathIterator.SEG_QUADTO -> prevPoint = new Point((int) coords[2], (int) coords[3]);
                case PathIterator.SEG_CUBICTO -> point = new Point((int) coords[4], (int) coords[5]);
            }
            if (point != null) {
                task.accept(prevPoint, coords);
                prevPoint = point;
            }
            pathIterator.next();
        }
    }

    public static void forEachQuadArc(PathIterator pathIterator, BiConsumer<Point, float[]> task) {
        float[] coords = new float[6];
        Point prevPoint = null;
        while (!pathIterator.isDone()) {
            Point point = null;
            switch (pathIterator.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO -> prevPoint = new Point((int) coords[0], (int) coords[1]);
                case PathIterator.SEG_CUBICTO -> prevPoint = new Point((int) coords[4], (int) coords[5]);
                case PathIterator.SEG_QUADTO -> point = new Point((int) coords[2], (int) coords[3]);
            }
            if (point != null) {
                task.accept(prevPoint, coords);
                prevPoint = point;
            }
            pathIterator.next();
        }
    }

    public static void forEachSegment(PathIterator pathIterator, TriConsumer<Integer, Point, float[]> task) {
        float[] coords = new float[6];
        Point prevPoint = null;
        while (!pathIterator.isDone()) {
            Point point = null;
            int segmentType = pathIterator.currentSegment(coords);

            switch (pathIterator.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO -> prevPoint = new Point((int) coords[0], (int) coords[1]);
                case PathIterator.SEG_LINETO -> point = new Point((int) coords[0], (int) coords[1]);
                case PathIterator.SEG_CUBICTO -> point = new Point((int) coords[4], (int) coords[5]);
                case PathIterator.SEG_QUADTO -> point = new Point((int) coords[2], (int) coords[3]);
            }

            if (segmentType != PathIterator.SEG_CLOSE) {
                task.accept(segmentType, prevPoint, coords);
                if (point != null) prevPoint = point;
            }
            pathIterator.next();
        }
    }

    public static List<Point2D> getPoints(Shape shape) {
        return getPoints(shape.getPathIterator(null));
    }

    public static List<Point2D> getPoints(PathIterator pathIterator) {
        List<Point2D> points = new ArrayList<>(5);
        forEachPoint(pathIterator, (x, y) -> points.add(new Point(x.intValue(), y.intValue())));
        return points;
    }


    public static Point2D snap(float snapDistance, Point point, Point2D... targets) {
        if (point == null) return null;
        if (targets == null || targets.length == 0) return point;
        for (Point2D target : targets) {
            if (point.distanceSq(target) < snapDistance * snapDistance) return target;
        }
        return point;
    }

    public static Point2D snap(float snapDistance, Point point, List<Point2D> targets) {
        if (point == null) return null;
        if (targets == null || targets.isEmpty()) return point;
        for (Point2D target : targets) {
            if (point.distanceSq(target) < snapDistance * snapDistance) return target;
        }
        return point;
    }

    public static Point2D snapClosest(float snapDistance, Point point, Point2D... targets) {
        if (point == null) return null;
        if (targets == null || targets.length == 0) return point;

        Point2D closestPoint = point;
        double closestDistanceSq = Double.MAX_VALUE;
        for (Point2D target : targets) {
            double distanceSq = point.distanceSq(target);
            if (distanceSq < snapDistance * snapDistance && distanceSq < closestDistanceSq) {
                closestPoint = target;
                closestDistanceSq = distanceSq;
            }
        }
        return closestPoint;
    }

    public static Point2D snapClosest(float snapDistance, Point point, List<Point2D> targets) {
        if (point == null) return null;
        if (targets == null || targets.isEmpty()) return point;

        Point2D closestPoint = point;
        double closestDistanceSq = Double.MAX_VALUE;
        for (Point2D target : targets) {
            double distanceSq = point.distanceSq(target);
            if (distanceSq < snapDistance * snapDistance && distanceSq < closestDistanceSq) {
                closestPoint = target;
                closestDistanceSq = distanceSq;
            }
        }
        return closestPoint;
    }



    private GraphUtils() {
        throw new AssertionError("No GraphUtils instances for you!");
    }

}
