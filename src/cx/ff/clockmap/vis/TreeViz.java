/* =============================================================================
 * The following code is based on the great TreeViz project.
 * 
 * http://www.randelshofer.ch/treeviz/
 *
 * Copyright (rc) 2008 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 * 
 * You can license the overall code either under Creative Commons
 * Attribution 3.0, the MIT license, or the GNU Lesser General License LGPL.
 * =============================================================================
 */
package cx.ff.clockmap.vis;

import cx.ff.clockmap.util.Complex;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;

public class TreeViz {

    TreeNode root;

    public TreeViz(TreeNode root) {
        this.root = root;
    }

    public void layout() {
       
        
        root.setTransform(new AffineTransform()); // reset for relayout
        double error = 0.01;
        if (root.getChildrenCount() == 0) {
        } else {
            for (int i = 0; i < root.getChildrenCount(); i++) {
                TreeNode treeNode = ((TreeNode) root.getChild(i));
                treeNode.layout();
            }
            ArrayList<TreeNode> tempNodes = root.getChildren();
            Collections.sort(tempNodes, new TreeNodeComparator());
            root.removeAllChildren();
            for (int i = 0; i < tempNodes.size(); i++) {
                root.addChild(tempNodes.get(i));
            }

            if (root.getChildrenCount() == 1) {
                ((TreeNode) root.getChild(0)).cx = 0;
                ((TreeNode) root.getChild(0)).cy = 0;
            }

            if (root.getChildrenCount() == 2) {
                TreeNode c0 = ((TreeNode) root.getChild(0));
                TreeNode c1 = ((TreeNode) root.getChild(1));
                double radius2 = c0.radius + c1.radius;
                c0.cx = c0.radius - radius2;
                c0.cy = 0;
                c1.cx = radius2 - c1.radius;
                c1.cy = 0;
            }


            if (root.getChildrenCount() > 2) {
                TreeNode ca = ((TreeNode) root.getChild(0));
                TreeNode cb = ((TreeNode) root.getChild(1));
                TreeNode cc = ((TreeNode) root.getChild(2));

                // compute the side lengths of the triangle which has
                // its corners in the center of each circle.
                double a = cb.radius + cc.radius;
                double b = ca.radius + cc.radius;
                double c = ca.radius + cb.radius;

                // compute the height rc
                double area = Math.sqrt(ca.radius * cb.radius * cc.radius * (ca.radius + cb.radius + cc.radius));
                double hc = 2 * area / c;

                ca.cx = -ca.radius;
                ca.cy = 0;
                cb.cx = cb.radius;
                cb.cy = 0;
                cc.cx = ca.cx + Math.sqrt(b * b - hc * hc);
                cc.cy = hc;
                Point2D.Double cp = new Point2D.Double();
                AffineTransform transform = new AffineTransform();

                double shift = (ca.radius - cb.radius);
                ca.cx += shift;
                cb.cx += shift;
                cc.cx += shift;

                if (root.getChildrenCount() > 3) {
                    double smallestRadius = ((TreeNode) root.getChild(root.getChildrenCount() - 1)).radius;
                    //children.get(children.size() - 1).radius;

                    ArrayList<TreeViz.Pair> pairs = new ArrayList<TreeViz.Pair>();
                    pairs.add(new TreeViz.Pair(cb, ca)); // outer edges
                    pairs.add(new TreeViz.Pair(ca, cc));
                    pairs.add(new TreeViz.Pair(cc, cb));

                    double innerSoddyRadius = innerSoddyRadius(ca.radius, cb.radius, cc.radius);
                    if (innerSoddyRadius >= smallestRadius) {
                        pairs.add(new TreeViz.Pair(ca, cb, innerSoddyRadius)); // inner pair
                        pairs.add(new TreeViz.Pair(cc, ca, innerSoddyRadius)); // inner pair
                        pairs.add(new TreeViz.Pair(cb, cc, innerSoddyRadius)); // inner pair
                    }

                    Point2D.Double closestPoint = new Point2D.Double();
                    int closestEdgeIndex = -1;
                    TreeViz.Pair closestEdge = null;
                    for (int i = 3; i < root.getChildrenCount(); i++) {
                        cc = ((TreeNode) root.getChild(i));
                        closestPoint.x = Double.MAX_VALUE;
                        closestPoint.y = Double.MAX_VALUE;
                        closestEdgeIndex = -1;
                        closestEdge = null;
                        for (int pairIndex = 0; pairIndex < pairs.size(); pairIndex++) {
                            boolean intersects;
                            TreeViz.Pair pair = pairs.get(pairIndex);
                            if (pair.innerSoddyRadius < cc.radius - error) {
                                intersects = true;
                            } else {

                                ca = pair.ca;
                                cb = pair.cb;

                                // compute the side lengths of the triangle which has
                                // its corners in the center of each circle.
                                a = cb.radius + cc.radius;
                                b = ca.radius + cc.radius;
                                c = ca.radius + cb.radius;

                                // compute the height rc
                                area = Math.sqrt(ca.radius * cb.radius * cc.radius * (ca.radius + cb.radius + cc.radius));
                                hc = 2 * area / c;

                                cp.x = Math.sqrt(b * b - hc * hc);
                                cp.y = hc;
                                double theta = Math.atan2(cb.cy - ca.cy, cb.cx - ca.cx);
                                transform.setToIdentity();
                                transform.translate(ca.cx, ca.cy);
                                transform.rotate(theta);
                                transform.transform(cp, cp);
                                // if we are farther away than the
                                // closest point we found so far,
                                // we can immediately abort intersection
                                // tests.
                                if (cp.x * cp.x + cp.y * cp.y
                                        >= closestPoint.x * closestPoint.x + closestPoint.y * closestPoint.y) {
                                    intersects = true;
                                } else {
                                    intersects = false;
                                    cc.cx = cp.x;
                                    cc.cy = cp.y;

                                    for (int j = 0; j < i; j++) {
                                        if (cc.intersects(((TreeNode) root.getChild(j)), error)) {
                                            // make the inner soddy radius smaller, because we can't fit 
                                            // our circle in here
                                            pair.innerSoddyRadius = cc.getIntersectionRadius(((TreeNode) root.getChild(j)));
                                            intersects = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!intersects) {
                                if (cc.cx * cc.cx + cc.cy * cc.cy < closestPoint.x * closestPoint.x + closestPoint.y * closestPoint.y) {
                                    closestPoint.x = cc.cx;
                                    closestPoint.y = cc.cy;
                                    closestEdgeIndex = pairIndex;
                                    closestEdge = pairs.get(closestEdgeIndex);
                                }
                            }
                        }
                        cc.cx = closestPoint.x;
                        cc.cy = closestPoint.y;
                        
                        ca = pairs.get(closestEdgeIndex).ca;
                        cb = pairs.get(closestEdgeIndex).cb;
                        innerSoddyRadius = innerSoddyRadius(ca.radius, cb.radius, cc.radius);
                        if (innerSoddyRadius >= smallestRadius) {
                            pairs.get(closestEdgeIndex).innerSoddyRadius = innerSoddyRadius;
                            pairs.add(new TreeViz.Pair(cc, ca, innerSoddyRadius));
                            pairs.add(new TreeViz.Pair(cb, cc, innerSoddyRadius));
                        } else {
                            pairs.remove(closestEdgeIndex);
                        }
                        pairs.add(new TreeViz.Pair(ca, cc));
                        pairs.add(new TreeViz.Pair(cc, cb));
                    }
                }

            }

            TreeNode temp = boundingCircle(root.getChildren());
            root.radius = temp.radius;
            root.setBounds(0, 0, root.radius * 2, root.radius * 2);

            for (int i = 0; i < root.getChildrenCount(); i++) {
                TreeNode treeNode = ((TreeNode) root.getChild(i));
                treeNode.setBounds(0, 0, treeNode.radius * 2, treeNode.radius * 2);
                treeNode.translate(treeNode.cx - treeNode.radius, treeNode.cy - treeNode.radius);
                treeNode.translate(-(temp.cx - temp.radius), -(temp.cy - temp.radius));
            }
        }
    }

    private static class Pair {

        public TreeNode ca;
        public TreeNode cb;
        public double innerSoddyRadius = Double.MAX_VALUE;

        public Pair(TreeNode ca, TreeNode cb) {
            this.ca = ca;
            this.cb = cb;
        }

        public Pair(TreeNode ca, TreeNode cb, double innerSoddyRadius) {
            this.ca = ca;
            this.cb = cb;
            this.innerSoddyRadius = innerSoddyRadius;
        }
    }

    /**
     * Computes the radius of the inner soddy circle for three tightly packed
     * circles.
     *
     * @param ra Radius of circle A
     * @param rb Radius of circle B
     * @param rc Radius of circle C
     * @return radius of the inner soddy circle
     */
    public static double innerSoddyRadius(double ra, double rb, double rc) {
        return (ra * rb * rc) / (ra * rc + ra * rb + rb * rc + Math.sqrt(4 * ra * rb * rc * (ra + rb + rc)));

    }

    /**
     * Computes the radius of the outer soddy circle for three tightly packed
     * circles.
     *
     * @param ra Radius of circle A
     * @param rb Radius of circle B
     * @param rc Radius of circle C
     * @return radius of the outer soddy circle
     */
    public static double outerSoddyRadius(double ra, double rb, double rc) {
        /*
         * // Solution using Descartes' theorem, // as described here:
         * http://en.wikipedia.org/wiki/Descartes%27_theorem double
         * k1,k2,k3,k4a,k4b; k1 = 1/ra; k2 = 1/rb; k3 = 1/rc; //k4a =
         * Math.abs(k1+k2+k3+2*Math.sqrt(k1*k2+k2*k3+k3*k1)); k4b =
         * Math.abs(k1+k2+k3-2*Math.sqrt(k1*k2+k2*k3+k3*k1)); return 1/k4b;
         */

        // Shorter solution:
        return Math.abs((ra * rb * rc) / (ra * rc + ra * rb + rb * rc - Math.sqrt(4 * ra * rb * rc * (ra + rb + rc))));
    }

    /**
     * Calculate the bounding box of all circles.
     *
     * @param circles
     * @return the bounding box.
     */
    public static Rectangle2D.Double boundingBox(ArrayList<TreeNode> circles) {
        double minx = Double.MAX_VALUE, maxx = Double.MIN_VALUE;
        double miny = Double.MAX_VALUE, maxy = Double.MIN_VALUE;

        for (TreeNode c : circles) {
            minx = Math.min(minx, c.cx - c.radius);
            maxx = Math.max(maxx, c.cx + c.radius);
            miny = Math.min(miny, c.cy - c.radius);
            maxy = Math.max(maxy, c.cy + c.radius);
        }

        return new Rectangle2D.Double(minx, miny, maxx - minx, maxy - miny);
    }

    /**
     * Calculate the bounding circle of all circles.
     *
     * @param circles
     * @return the bounding circle.
     */
    public TreeNode boundingCircle(ArrayList<TreeNode> circles) {
        // The following algorithm calculates a bounding circle
        // which is tangent to at least one of the given circles


        // Bounding soddy circle: Try to find a bounding circle which is tangent
        // to three of the given circles (soddy circle), and contains
        // them all.
        TreeNode outerSoddy = null;
        if (circles.size() >= 3) {
            outerSoddy = outerSoddyCircle(circles.get(0), circles.get(1), circles.get(2));
            for (TreeNode c : circles) {
                double dist = Math.sqrt(
                        (outerSoddy.cx - c.cx) * (outerSoddy.cx - c.cx)
                        + (outerSoddy.cy - c.cy) * (outerSoddy.cy - c.cy));
                outerSoddy.radius = Math.max(outerSoddy.radius, dist + c.radius);
            }
        }


        // Bounding tangent circle: Find a bounding circle which is tangent
        // to one of the given circles
        Rectangle2D.Double bbox = boundingBox(circles);
        TreeNode bc = new TreeNode(root.vis, bbox.getCenterX(), bbox.getCenterY(),
                Math.max(bbox.width, bbox.height) / 2);
        for (TreeNode c : circles) {
            double dist = Math.sqrt(
                    (bc.cx - c.cx) * (bc.cx - c.cx)
                    + (bc.cy - c.cy) * (bc.cy - c.cy));
            bc.radius = Math.max(bc.radius, dist + c.radius);
        }

        // Return the smaller of the bounding tangent circle and the bounding
        // soddy circle (if it exists).
        return (outerSoddy == null || bc.radius < outerSoddy.radius) ? bc : outerSoddy;
    }

    /**
     * Computes the outer soddy circle for three tightly packed circles.
     *
     * @param circleA Circle ra
     * @param circleB Circle rb
     * @param circleC Circle rc
     * @return Outer soddy circle. The Outer soddy circle has a radius of 0, if
     * there is no solution, that is, if the outer soddy circle is a straight
     * line.
     */
    public TreeNode outerSoddyCircle(TreeNode circleA, TreeNode circleB, TreeNode circleC) {
        // radii of the three tightyl packed circles
        double ra = circleA.radius;
        double rb = circleB.radius;
        double rc = circleC.radius;

        // Solution using Descartes' theorem,
        // as described here: http://en.wikipedia.org/wiki/Descartes%27_theorem
        double k1, k2, k3, k4;
        k1 = 1 / ra;
        k2 = 1 / rb;
        k3 = 1 / rc;
        k4 = Math.abs(k1 + k2 + k3 - 2 * Math.sqrt(k1 * k2 + k2 * k3 + k3 * k1));
        Complex q1, q2, q3, q4;
        q1 = new Complex(k1, 0).mul(new Complex(circleA.cx, circleA.cy));
        q2 = new Complex(k2, 0).mul(new Complex(circleB.cx, circleB.cy));
        q3 = new Complex(k3, 0).mul(new Complex(circleC.cx, circleC.cy));
        q4 = q1.add(q2).add(q3).sub(new Complex(2, 0).mul(q1.mul(q2).add(q2.mul(q3)).add(q3.mul(q1)).sqrt()));

        Complex z = q4.div(new Complex(k4, 0));

        // if the formula is not solveable, we return an empty circle.
        // we compute the second solution.
        if (z.isNaN() || Double.isNaN(1 / k4)) {
            return new TreeNode(root.vis, 0, 0, 0);
            /*
             * k4 = Math.abs(k1 + k2 + k3 + 2 * Math.sqrt(k1 * k2 + k2 * k3 + k3
             * * k1)); q4 = q1.add(q2).add(q3).add(new
             * Complex(2,0).mul(q1.mul(q2).add(q2.mul(q3)).add(q3.mul(q1)).sqrt()));
             * z = q4.div(new Complex(k4, 0));
             */
        }
        return new TreeNode(root.vis, -z.real(), -z.img(), 1 / k4);
    }
}
