package cx.ff.clockmap.vis;

import cx.ff.clockmap.Main;
import cx.ff.clockmap.util.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.*;
import java.util.ArrayList;
import org.piccolo2d.PNode;
import org.piccolo2d.util.PPaintContext;

public class TreeNode extends PNode {

    private Ellipse2D ellipse;
    private String name;
    private long[] values = null;
    private long[] minmaxValue = new long[2];

    public long[] getMinMaxValue() {
        return minmaxValue;
    }
    protected double radius;
    protected double cx;
    protected double cy;
    protected CircularVisualization vis;
    private int threshold;
    private Normalizer normalizer = null;
    private TreeViz treeViz = new TreeViz(this);
    public boolean showAlways = false;

    private long[] cachedMinMax = null;

    public TreeNode(CircularVisualization vis, double x, double y, double r) {
        this.vis = vis;
        this.cx = x;
        this.cy = y;
        this.radius = r;

         
    }

    public TreeNode(CircularVisualization vis, String name) {
        this.vis = vis;
        this.name = name;
        this.addAttribute("tooltip", name);
        this.radius = 50;
        ellipse = new Ellipse2D.Double();

    }

    public TreeNode(CircularVisualization vis, String name, long[] values, double radius, long total) {
        this.vis = vis;
        this.name = name;
        this.addAttribute("tooltip", name);
        this.values = values;
        this.minmaxValue = getMinMax(values);

        this.radius = radius;

        ellipse = new Ellipse2D.Double();

    }

    public void layout() {
        treeViz.layout();
    }

    public long[] getValues() {
        if (values != null) {
            return this.values;
        } else {
            long[] val = new long[24];
            for (int i = 0; i < this.getChildrenCount(); i++) {
                long[] vChild = ((TreeNode) this.getChild(i)).getValues();
                for (int j = 0; j < 24; j++) {
                    val[j] += vChild[j];
                }
            }

            minmaxValue = getMinMax(val);

            return val;
        }
    }

    public boolean hasValues() {
        if (values != null) {
            return true;
        } else {
            return false;
        }
    }

    public Ellipse2D getEllipse() {
        return ellipse;
    }

    @Override
    public boolean setBounds(double x, double y, double width, double height) {
        if (super.setBounds(x, y, width, height)) {
            ellipse.setFrame(x, y, width, height);
            return true;
        }
        return false;
    }

    @Override
    public boolean intersects(Rectangle2D aBounds) {
        return getEllipse().intersects(aBounds);
    }

    public boolean contains(Point2D p) {
        Point2D temp = new Point2D.Double(p.getX() - this.getGlobalBounds().x, p.getY() - this.getGlobalBounds().y);
        return getEllipse().contains(temp);
    }

    @Override
    public void addChild(PNode child) {
        super.addChild(child);
    }

    @Override
    public PNode removeChild(PNode child) {
        PNode temp = super.removeChild(child);
        if (getChildrenCount() == 0) {
            this.radius = 50;
        }
        return temp;
    }

    public int getLevel() {
        int level = -1;
        TreeNode temp = this;
        while (temp != null) {
            try {
                temp = (TreeNode) temp.getParent();
            } catch (ClassCastException e) {
                return level;
            }

            level++;
        }
        return level;
    }

    private void paintBorder(Graphics2D g2, boolean singleClock, boolean group) {

        double x = getX();
        double y = getY();

        Ellipse2D.Double e = new Ellipse2D.Double(x, y, getWidth(), getHeight());

        if (singleClock) {
            g2.setColor(Main.mainProperties.getClockBackground());

            g2.setStroke(new BasicStroke(0F));
            g2.fill(e);
        } else {

            if (group) {
                g2.setColor(Main.mainProperties.getBackground());

                g2.setStroke(new BasicStroke(0F));
                g2.fill(e);
            } else {
                g2.setColor(Main.mainProperties.getClockGroupBackground());

                g2.setStroke(new BasicStroke(0F));
                g2.fill(e);
            }
        }

        g2.setStroke(new BasicStroke(0.3F));

        g2.setColor(Main.mainProperties.getLines());
        g2.draw(e);

//        
//        double x = getX();
//        double y = getY();
//        g2.setStroke(new BasicStroke(0.3F));
//        Ellipse2D.Double e = new Ellipse2D.Double(x, y, getWidth(), getHeight());
//        g2.setPaint(vis.clockColors.lines);
//      
//        g2.draw(e);
//        g2.setPaint(vis.clockColors.lines);
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        // paintContext.setRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);

        double scale = paintContext.getScale();

        g2.setStroke(new BasicStroke(1.0f));

        threshold = 0;

        if (scale > 0.1) {
            threshold = 1;
        }

        if (scale > 0.2) {
            threshold = 2;
        }

        int level = getLevel();

        if (level == threshold) {

            if (!this.showAlways) {

                paintBorder(g2, true, false);
                paintValuesAlternativeClock(g2);

            } else {
                paintBorder(g2, false, true);
            }

        } else {

            PNode pn = this.getParent();

            if (((pn instanceof TreeNode) && ((TreeNode) pn).showAlways) || level < threshold) {

                if (values != null) {

                    if (!this.showAlways) {
                        paintBorder(g2, true, false);
                        paintValuesAlternativeClock(g2);
                    } else {
                        paintBorder(g2, false, true);
                    }
                } else {
                    if (level == 1) {
                        paintBorder(g2, false, false);
                    } else {
                        paintBorder(g2, false, true);
                    }

                }
            }

        }

    }

    public int getThreshold() {
        return threshold;
    }

    public String getTooltipValue(double x, double y) {

        long val[] = getValues();

        double size = radius / 2;

        if (new Ellipse2D.Double(radius - size / 2.0, radius - size / 2.0, size, size).contains(x, y)) {
            long total = 0;
            for (int i = 0; i < 24; i++) {
                total += val[i];
            }
            if (this.getChildrenCount() == 0) {

                String tooltip = Main.mainProperties.getTooltipHostText();
                tooltip = tooltip.replace("$NODENAME$", getNodeName());
                tooltip = tooltip.replace("$VALUE$", StringUtils.humanReadableNumber(total));
                tooltip = tooltip.replace("$BYTES$", StringUtils.humanReadableByteCount(total, true));

                return tooltip;

            } else {

                String tooltip = Main.mainProperties.getTooltipSubnetText();
                tooltip = tooltip.replace("$NODENAME$", getNodeName());
                tooltip = tooltip.replace("$VALUE$", StringUtils.humanReadableNumber(total));
                tooltip = tooltip.replace("$BYTES$", StringUtils.humanReadableByteCount(total, true));

                return tooltip;

            }

        }

        for (int i = 0; i < 24; i++) {

            Arc2D.Double arc;

            long temp = val[i];

            arc = new Arc2D.Double(Arc2D.PIE);
            arc.setFrame(
                    this.getX(),
                    this.getY(),
                    2 * radius,
                    2 * radius);

            arc.setAngleStart(90 - (i * (360f / 24f)));
            arc.setAngleExtent(-(360f / 24f));
            if (arc.contains(x, y)) {

                String tooltip = Main.mainProperties.getTooltipHourText();
                tooltip = tooltip.replace("$NODENAME$", getNodeName());
                tooltip = tooltip.replace("$BYTES$", StringUtils.humanReadableByteCount(temp, true));
                tooltip = tooltip.replace("$VALUE$", StringUtils.humanReadableNumber(temp));
                tooltip = tooltip.replace("$HOUR$", i + ":00 - " + (i + 1) + ":00");

                return tooltip;

            }

        }

        return null;
    }

    public double getIntersectionRadius(TreeNode that) {
        double dist
                = /*
                 * Math.sqrt(
                 */ (this.cx - that.cx) * (this.cx - that.cx)
                + (this.cy - that.cy) * (this.cy - that.cy)/*
                 * )
                 */;

        return Math.sqrt(dist) - that.radius;
    }

    public boolean intersects(TreeNode that, double error) {
        double dist
                = /*
                 * Math.sqrt(
                 */ (this.cx - that.cx) * (this.cx - that.cx)
                + (this.cy - that.cy) * (this.cy - that.cy)/*
                 * )
                 */;

        return dist < (this.radius + that.radius) * (this.radius + that.radius) - error;

    }

    private long[] getMinMax(long[] array) {

        long[] r = new long[2];
        r[0] = Long.MAX_VALUE;
        r[1] = Long.MIN_VALUE;

        for (int i = 0; i < array.length; i++) {
            if (array[i] < r[0]) {
                r[0] = array[i];
            }

            if (array[i] > r[1]) {
                r[1] = array[i];
            }
        }

        return r;

    }

    private void paintValuesAlternativeClock(Graphics2D g2) {

        long val[] = getValues();

        if (this.getLevel() == 0) {

            // for top-most level local clock min-max
            long[] l = getMinMax(val);
                    
            normalizer = vis.getSelectedNormalizer(minmaxValue[0], minmaxValue[1]);
                    
            

        } else {

            long valueMin = Long.MAX_VALUE;
            long valueMax = Long.MIN_VALUE;

            if (cachedMinMax == null) {

                TreeNode p = (TreeNode) this.getParent();

                // find minmax values in siblings
                for (Object n : p.getChildrenReference()) {

                    TreeNode t = (TreeNode) n;

                    if (t.getLevel() == this.getLevel()) {
                        if (t.getMinMaxValue()[1] > valueMax) {
                            valueMax = t.getMinMaxValue()[1];
                        }

                        if (t.getMinMaxValue()[0] < valueMin) {
                            valueMin = t.getMinMaxValue()[0];
                        }

                    }

                }
                cachedMinMax = new long[2];
                cachedMinMax[0] = valueMin;
                cachedMinMax[1] = valueMax;

            } else {
                valueMin = cachedMinMax[0];
                valueMax = cachedMinMax[1];
            }

            
            // sibling clock min-max            
             normalizer = vis.getSelectedNormalizer(valueMin, valueMax);
                         
          
        }


        for (int i = 0; i < 24; i++) {

            if (val[i] != 0) {
                Arc2D.Double arc;

                int selectedColor = Math.round(Math.round(normalizer.normalize(val[i]) * (Main.mainProperties.getColormap().getLength() - 1)));
                selectedColor = Math.min(selectedColor, Main.mainProperties.getColormap().getLength() - 1);
                Color c = Main.mainProperties.getColormap().getColorAt(selectedColor);

                Point2D center = new Point2D.Float((float) radius, (float) radius);

                float[] dist = {0.1f, 0.5f, 1.0f};
                Color[] colors = {Color.BLACK, c, Color.BLACK};
                RadialGradientPaint gp
                        = new RadialGradientPaint(center, (float) radius, dist, colors);

                g2.setPaint(gp);

                arc = new Arc2D.Double(Arc2D.PIE);
                arc.setFrame(
                        this.getX(),
                        this.getY(),
                        2 * radius,
                        2 * radius);

                arc.setAngleStart(90 - (i * (360f / 24f)));
                arc.setAngleExtent(-(360f / 24f));

                g2.fill(arc);

                double winkel = 2 * Math.PI / 24;

                double clockX = Math.cos(i * winkel) * (radius - 0.05); // 0.05 halbe basicstroke
                double clockY = Math.sin(i * winkel) * (radius - 0.05);

                clockX += radius;
                clockY += radius;

                double clockInnenX = Math.cos(i * winkel) * (radius - 1);
                double clockInnenY = Math.sin(i * winkel) * (radius - 1);

                clockInnenX += (radius);
                clockInnenY += (radius);

            }

        }

        double winkel = 2 * Math.PI / 24;
        for (int i = 0; i < 24; i++) {
            double clockX = Math.cos(i * winkel) * (radius - 0.05); // 0.05 halbe basicstroke
            double clockY = Math.sin(i * winkel) * (radius - 0.05);

            clockX += radius;
            clockY += radius;

            double dist;
            if (i == 0 || i == 6 || i == 12 || i == 18) {
                dist = 2;
                double clockInnenX = Math.cos(i * winkel) * (radius - dist);
                double clockInnenY = Math.sin(i * winkel) * (radius - dist);

                clockInnenX += (radius);
                clockInnenY += (radius);

                g2.setStroke(new BasicStroke(0.3f));
                g2.setColor(Main.mainProperties.getLines());
                if (i == 0) { // 6
                    g2.draw(new Line2D.Double(clockX - 0.1, clockY, clockInnenX, clockInnenY));
                }
                if (i == 6) { // 12
                    g2.draw(new Line2D.Double(clockX, clockY - 0.1, clockInnenX, clockInnenY));
                }
                if (i == 12) { // 18
                    g2.draw(new Line2D.Double(clockX + 0.1, clockY, clockInnenX, clockInnenY));
                }
                if (i == 18) { //0
                    g2.draw(new Line2D.Double(clockX, clockY + 0.1, clockInnenX, clockInnenY));
                }

            } else {
                dist = 1;
                double clockInnenX = Math.cos(i * winkel) * (radius - dist);
                double clockInnenY = Math.sin(i * winkel) * (radius - dist);

                clockInnenX += (radius);
                clockInnenY += (radius);

                g2.setStroke(new BasicStroke(0.1f));
                g2.setColor(Main.mainProperties.getLines());
                g2.draw(new Line2D.Double(clockX, clockY, clockInnenX, clockInnenY));
            }

        }

        if (!vis.getSearchString().equals("") && getNodeName().contains(vis.getSearchString())) {
            g2.setPaint(Main.mainProperties.getSelected());
        } else {
            g2.setPaint(Color.BLACK);
        }

        double size = radius / 2;
        g2.fill(new Ellipse2D.Double(radius - size / 2.0, radius - size / 2.0, size, size));
    }

    public ArrayList<TreeNode> getChildren() {
        ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
        for (int i = 0; i < this.getChildrenCount(); i++) {
            nodes.add((TreeNode) this.getChild(i));
        }
        return nodes;
    }

    public double getRadius() {
        return this.radius;
    }

    public String getNodeName() {
        return this.name;
    }

    public void toggleShowAlways() {
        showAlways = !showAlways;
        this.repaint();
    }

    public void resetShowAlways() {
        showAlways = false;
        this.repaint();
    }
}
