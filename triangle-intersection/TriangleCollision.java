package com.app.example;

class Point {
    float x;
    float y;
    
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }
}

class Triangle {
    Point p1;
    Point p2;
    Point p3;
    Point[] pointList;
    
    public Triangle(Point p1, Point p2, Point p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.pointList = new Point[3];
        this.pointList[0] = p1;
        this.pointList[1] = p2;
        this.pointList[2] = p3;
    }
}

public class TriangleCollision {
    
    private static float sign(Point p1, Point p2, Point p3) {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
    }
    
    public static boolean pointInTriangle(Point pt, Triangle tri) {
        float d1, d2, d3;
        boolean has_neg, has_pos;
        
        d1 = sign(pt, tri.pointList[0], tri.pointList[1]);
        d2 = sign(pt, tri.pointList[1], tri.pointList[2]);
        d3 = sign(pt, tri.pointList[2], tri.pointList[0]);
        
        has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);
        
        return !(has_neg && has_pos);
    }
    
    private static double det2D(Triangle triangle) {
        return triangle.p1.x * (triangle.p2.y - triangle.p3.y)
             + triangle.p2.x * (triangle.p3.y - triangle.p1.y)
             + triangle.p3.x * (triangle.p1.y - triangle.p2.y);
    }
    
    private static void checkTriWinding(Triangle t, boolean allowReversed) throws Exception {
        double detTri = det2D(t);
        
        if (detTri < 0.0) {
            if (allowReversed) {
                Triangle tReverse = new Triangle(t.p3, t.p2, t.p1);
                checkTriWinding(tReverse, false);
            } else {
                throw new Exception("triangle has wrong winding direction");
            }
        }
    }
    
    private static boolean boundaryCollideChk(Triangle t, double eps) {
        return det2D(t) < eps;
    }
    
    private static boolean boundaryCollideChk(Point p1, Point p2, Point p3, double eps) {
        Triangle t = new Triangle(p1, p2, p3);
        return boundaryCollideChk(t, eps);
    }
    
    private static boolean boundaryDoesntCollideChk(Triangle t, double eps) {
        return det2D(t) <= eps;
    }
    
    private static boolean boundaryDoesntCollideChk(Point p1, Point p2, Point p3, double eps) {
        Triangle t = new Triangle(p1, p2, p3);
        return boundaryDoesntCollideChk(t, eps);
    }
    
    public static boolean triangleTriangleCollision(Triangle triangle1, Triangle triangle2,
                                                   double eps, boolean allowReversed, boolean onBoundary) {
        try {
            // Triangles must be expressed anti-clockwise
            checkTriWinding(triangle1, allowReversed);
            checkTriWinding(triangle2, allowReversed);
            
            // For edge E of triangle 1
            for (int i = 0; i < 3; i++) {
                int j = (i + 1) % 3;
                
                if (onBoundary) {
                    // Check all points of triangle 2 lay on the external side of the edge E
                    if (boundaryCollideChk(triangle1.pointList[i], triangle1.pointList[j], triangle2.pointList[0], eps) &&
                        boundaryCollideChk(triangle1.pointList[i], triangle1.pointList[j], triangle2.pointList[1], eps) &&
                        boundaryCollideChk(triangle1.pointList[i], triangle1.pointList[j], triangle2.pointList[2], eps)) {
                        return false;
                    }
                } else {
                    if (boundaryDoesntCollideChk(triangle1.pointList[i], triangle1.pointList[j], triangle2.pointList[0], eps) &&
                        boundaryDoesntCollideChk(triangle1.pointList[i], triangle1.pointList[j], triangle2.pointList[1], eps) &&
                        boundaryDoesntCollideChk(triangle1.pointList[i], triangle1.pointList[j], triangle2.pointList[2], eps)) {
                        return false;
                    }
                }
                
                if (onBoundary) {
                    // Check all points of triangle 1 lay on the external side of the edge E
                    if (boundaryCollideChk(triangle2.pointList[i], triangle2.pointList[j], triangle1.pointList[0], eps) &&
                        boundaryCollideChk(triangle2.pointList[i], triangle2.pointList[j], triangle1.pointList[1], eps) &&
                        boundaryCollideChk(triangle2.pointList[i], triangle2.pointList[j], triangle1.pointList[2], eps)) {
                        return false;
                    }
                } else {
                    if (boundaryDoesntCollideChk(triangle2.pointList[i], triangle2.pointList[j], triangle1.pointList[0], eps) &&
                        boundaryDoesntCollideChk(triangle2.pointList[i], triangle2.pointList[j], triangle1.pointList[1], eps) &&
                        boundaryDoesntCollideChk(triangle2.pointList[i], triangle2.pointList[j], triangle1.pointList[2], eps)) {
                        return false;
                    }
                }
            }
            
            // The triangles collide
            return true;
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
    
    // Overloaded method with default parameters
    public static boolean triangleTriangleCollision(Triangle triangle1, Triangle triangle2) {
        return triangleTriangleCollision(triangle1, triangle2, 0.0, true, true);
    }
    
    public static void main(String[] args) {
        // Test point in triangle
        Point p1 = new Point(5, 7);
        Point p2 = new Point(3, 4);
        Point p3 = new Point(3, 3);
        
        Triangle t1 = new Triangle(new Point(2, 2), new Point(5, 6), new Point(10, 0));
        
        System.out.println("Point p1 " + (pointInTriangle(p1, t1) ? "is" : "is not") + " in triangle t1");
        System.out.println("Point p2 " + (pointInTriangle(p2, t1) ? "is" : "is not") + " in triangle t1");
        System.out.println("Point p3 " + (pointInTriangle(p3, t1) ? "is" : "is not") + " in triangle t1");
        
        // Test triangle-triangle collision
        Triangle tri1 = new Triangle(new Point(3, 6), new Point(6, 5), new Point(6, 7));
        Triangle tri2 = new Triangle(new Point(4, 2), new Point(1, 5), new Point(6, 4));
        Triangle tri3 = new Triangle(new Point(3, 12), new Point(9, 8), new Point(9, 12));
        Triangle tri4 = new Triangle(new Point(3, 10), new Point(5, 9), new Point(5, 13));
        
        boolean collision1 = triangleTriangleCollision(tri1, tri2);
        System.out.println("Triangles t1 and t2 " + (collision1 ? "do" : "do not") + " collide");
        
        boolean collision2 = triangleTriangleCollision(tri3, tri4);
        System.out.println("Triangles t3 and t4 " + (collision2 ? "do" : "do not") + " collide");
        
        boolean collision3 = triangleTriangleCollision(tri1, tri3);
        System.out.println("Triangles t1 and t3 " + (collision3 ? "do" : "do not") + " collide");
    }
}
