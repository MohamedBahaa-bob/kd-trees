
import java.util.ArrayList;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private class Node {
        private Node left;
        private Node right;
        private int numberOfNodes;
        private final boolean isVertical;
        private final Point2D point;
        private final RectHV rectangle;

        public Node(Point2D point, boolean isVertical, RectHV rect) {
            if (point == null)
                throw new NullPointerException();
            this.point = point;
            this.isVertical = isVertical;
            numberOfNodes = 1;
            this.rectangle = rect;
        }

        public void draw(Point2D parent) {
            StdDraw.point(point.x(), point.y());
            if (isVertical) {
                StdDraw.setPenColor(StdDraw.MAGENTA);
                if (parent == null)
                    StdDraw.line(point.x(), 0, point.x(), 1);
                else if (Point2D.Y_ORDER.compare(parent, point) < 0)
                    StdDraw.line(point.x(), parent.y(), point.x(), 1);
                else
                    StdDraw.line(point.x(), 0, point.x(), parent.y());
            } else {
                StdDraw.setPenColor(StdDraw.BLUE);
                if (Point2D.X_ORDER.compare(parent, point) < 0)
                    StdDraw.line(parent.x(), point.y(), 1, point.y());
                else
                    StdDraw.line(0, point.y(), parent.x(), point.y());
            }
        }
    }

    private Node root;
    private ArrayList<Point2D> includedPoints;
    private Point2D nearest;

    public KdTree() {
        root = null;
    }

    /*
     * public Node root() { return root; }
     */

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size(root);
    }

    private int size(Node current) {
        if (current == null)
            return 0;
        return current.numberOfNodes;
    }

    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        root = insert(p, root, true, 0, 0, 1, 1);
    }

    private Node insert(Point2D p, Node current, boolean isVertical, double x1, double y1, double x2, double y2) {
        if (current == null)
            current = new Node(p, isVertical, new RectHV(x1, y1, x2, y2));
        else if (p.equals(current.point))
            return current;
        else if (isVertical) {
            if (Point2D.X_ORDER.compare(p, current.point) < 0)
                current.left = insert(p, current.left, !isVertical, x1, y1, current.point.x(), y2);
            else
                current.right = insert(p, current.right, !isVertical, current.point.x(), y1, x2, y2);
        } else {
            if (Point2D.Y_ORDER.compare(p, current.point) < 0)
                current.left = insert(p, current.left, !isVertical, x1, y1, x2, current.point.y());
            else
                current.right = insert(p, current.right, !isVertical, x1, current.point.y(), x2, y2);
        }
        current.numberOfNodes = 1 + size(current.left) + size(current.right);
        return current;
    }

    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        return contains(root, p, true);
    }

    private boolean contains(Node current, Point2D p, boolean isVertical) {
        if (current == null)
            return false;
        if (p.equals(current.point))
            return true;
        if (isVertical) {
            if (Point2D.X_ORDER.compare(p, current.point) < 0)
                return contains(current.left, p, !isVertical);
            else
                return contains(current.right, p, !isVertical);
        } else {
            if (Point2D.Y_ORDER.compare(p, current.point) < 0)
                return contains(current.left, p, !isVertical);
            else
                return contains(current.right, p, !isVertical);
        }
    }

    public void draw() {
        draw(root, null);
    }

    private void draw(Node current, Node parent) {
        if (current == null)
            return;
        current.draw(parent.point);
        draw(current.left, current);
        draw(current.right, current);
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException();
        includedPoints = new ArrayList<Point2D>();
        range(root, rect);
        return includedPoints;
    }

    private void range(Node current, RectHV rect) {
        if (current == null)
            return;
        if (!current.rectangle.intersects(rect))
            return;
        if (rect.contains(current.point))
            includedPoints.add(current.point);
        range(current.left, rect);
        range(current.right, rect);
    }

    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        if (isEmpty())
            return null;
        nearest = root.point;
        nearest(p, root);
        return nearest;
    }

    private void nearest(Point2D p, Node current) {
        if (current == null)
            return;
        if (p.equals(current.point))
            return;
        if (current.point.distanceSquaredTo(p) < nearest.distanceSquaredTo(p))
            nearest = current.point;
        if (current.rectangle.distanceSquaredTo(p) >= nearest.distanceSquaredTo(p))
            return;
        if (current.isVertical)
            if (Point2D.X_ORDER.compare(p, current.point) < 0) {
                nearest(p, current.left);
                nearest(p, current.right);
            } else {
                nearest(p, current.right);
                nearest(p, current.left);
            }
        else if (Point2D.Y_ORDER.compare(p, current.point) < 0) {
            nearest(p, current.left);
            nearest(p, current.right);
        } else {
            nearest(p, current.right);
            nearest(p, current.left);
        }
    }

    public static void main(String[] args) {

        /*
         * KdTree kd = new KdTree(); System.out.println(kd.isEmpty()); Point2D p = new
         * Point2D(0.1, 0.2); Point2D p1 = new Point2D(0.0, 0.25); Point2D p2 = new
         * Point2D(0.7, 0.2); Point2D p3 = new Point2D(0.6, 0.55); Point2D p4 = new
         * Point2D(0.2, 0.85); Point2D p6 = new Point2D(0.3, 0.54); Point2D p7 = new
         * Point2D(0.10, 0.97); kd.insert(p7); kd.insert(p6); kd.insert(p);
         * kd.insert(p4); kd.insert(p3); kd.insert(p2); kd.insert(p1);
         * System.out.println(kd.isEmpty()); System.out.println(kd.size());
         * System.out.println(kd.contains(p3)); //
         * System.out.println(kd.root().point.toString()); //
         * System.out.println(kd.root().right.point.toString()); //
         * System.out.println(kd.root().left.point.toString()); //
         * System.out.println(kd.root().right.right.point.toString()); Iterable<Point2D>
         * a = kd.range(new RectHV(0, 0.1, 1, 0.81)); for (Point2D pm : a) {
         * System.out.println(pm.toString()); }
         * //System.out.println(kd.nearest(p6).toString());
         */

    }
}
