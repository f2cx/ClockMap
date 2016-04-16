package cx.ff.clockmap.vis;

import cx.ff.clockmap.Main;
import cx.ff.clockmap.color.ColorMapLegend;
import cx.ff.clockmap.data.Clock;
import cx.ff.clockmap.data.Memory;
import cx.ff.clockmap.util.LinearNormalizer;
import cx.ff.clockmap.util.LogNormalizer;
import cx.ff.clockmap.util.Normalizer;
import cx.ff.clockmap.util.SquareRootNormalizer;
import cx.ff.clockmap.util.StringUtils;
import java.awt.Font;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.piccolo2d.PCamera;
import org.piccolo2d.PCanvas;
import org.piccolo2d.PLayer;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PZoomEventHandler;
import org.piccolo2d.nodes.PText;
import org.piccolo2d.util.PBounds;
import org.piccolo2d.util.PPaintContext;

public class CircularVisualization extends PCanvas {

    private TreeNode rootNode;
    private PLayer layer;
    private PCamera camera;
    private PText tooltip;
    private final HashMap<String, TreeNode> parents = new HashMap<>();
    private PNode selectedNode;
    private final HashMap<String, TreeNode> hash = new HashMap<>();
    private final HashSet<TreeNode> selectedNodes = new HashSet<>();
    private int counter = 0;

    final public int THRESHOLD = 2;
    public long maxtotal = 0;
    private final JTextField text = new JTextField();
    private final JComboBox box = new JComboBox();
    private final JSlider slider;

    final ColorMapLegend mapLegend;

    public CircularVisualization() {
        UIManager.put("Slider.paintValue", false);
        this.slider = new JSlider();
        this.mapLegend = new ColorMapLegend();

    }

    private void initialize() {

        this.selectedNode = rootNode;

        this.setBackground(Main.mainProperties.getOuterBackground());

        Collection allNodes = rootNode.getAllNodes();
        Iterator iterator = allNodes.iterator();
        while (iterator.hasNext()) {
            TreeNode t = (TreeNode) iterator.next();
            hash.put(t.getNodeName(), t);
        }

        layer = getLayer();
        camera = getCamera();
        layer.addChild(rootNode);

        //camera.setViewBounds(selectedNode.getBounds());
        tooltip = new PText();
        tooltip.setPaint(Main.mainProperties.getTextBackground());
        tooltip.setTextPaint(Main.mainProperties.getText());
        tooltip.setFont(new Font("sansserif", Font.BOLD, 24));
        tooltip.setPickable(false);
        camera.addChild(tooltip);

        createMapTypeButtons();

        camera.addInputEventListener(new PBasicInputEventHandler() {

            @Override
            public void mouseMoved(final PInputEvent event) {
                updateToolTip(event);
            }

            @Override
            public void mouseDragged(final PInputEvent event) {
                updateToolTip(event);
            }

            public void updateToolTip(final PInputEvent event) {
                try {

                    PNode n = event.getPickedNode();

                    TreeNode t = null;
                    try {
                        t = (TreeNode) n;
                    } catch (ClassCastException e) {
                    }

                    String s = "";

                    if (t != null) {
                        int level = t.getLevel();
                        int threshold = t.getThreshold();

                        if (((TreeNode) t.getParent()).showAlways) {
                            n = t;

                        } else {
                            if (threshold < level) {
                                int temp = level - threshold;
                                for (int i = 0; i < temp; i++) {
                                    n = n.getParent();
                                }
                            }

                        }

                        Point2D position = event.getPosition();
                        PBounds globalBounds = n.getGlobalBounds();
                        if (((TreeNode) n).getLevel() >= threshold || n.getChildrenCount() == 0) {
                            s = ((TreeNode) n).getTooltipValue(position.getX() - globalBounds.getX(), position.getY() - globalBounds.getY());
                        }

                    }

                    if (n.getAttribute("tooltip") != null) {
                        if (s != null && s.trim().length() != 0) {
                            final String tooltipString = " " + s;
                            //final String tooltipString = " " + (String) n.getAttribute("tooltip") + " " + s;
                            final Point2D p = event.getCanvasPosition();

                            event.getPath().canvasToLocal(p, camera);

                            tooltip.setText(tooltipString);
                            tooltip.setOffset(p.getX() + 8, p.getY() - 8);
                            tooltip.setVisible(true);
                        } else {
                            tooltip.setVisible(false);
                        }

                    } else {
                        tooltip.setVisible(false);
                    }

                } catch (Exception e) {

                }

            }
        }
        );

        camera.addInputEventListener(new PBasicInputEventHandler() {

            public TreeNode getHoverNode(final PInputEvent event) {
                PNode n = event.getPickedNode();

                TreeNode t = null;
                try {
                    t = (TreeNode) n;
                } catch (ClassCastException e) {
                }

                if (t != null) {
                    int level = t.getLevel();
                    int threshold = t.getThreshold();

                    if (((TreeNode) t.getParent()).showAlways) {
                        n = t;

                    } else {
                        if (threshold < level) {
                            int temp = level - threshold;
                            for (int i = 0; i < temp; i++) {
                                n = n.getParent();
                            }
                        }

                    }

                    if (((TreeNode) n).getLevel() >= threshold || n.getChildrenCount() == 0) {
                        return ((TreeNode) n);
                    }

                }
                return null;
            }

            @Override
            public void mouseClicked(PInputEvent event) {

                if (event.getClickCount() == 1) {

                    selectedNode = getHoverNode(event);

                    if (selectedNode != null && ((TreeNode) selectedNode).getLevel() == THRESHOLD) {

                        ((TreeNode) selectedNode).toggleShowAlways();

                        if (((TreeNode) selectedNode).showAlways) {
                            selectedNodes.add((TreeNode) selectedNode);
                        } else {
                            selectedNodes.remove((TreeNode) selectedNode);
                        }
                    }

                }

            }
        });

        final float MIN_SCALE = .0001f;
        final float MAX_SCALE = 2500;

        this.setZoomEventHandler(new PZoomEventHandler() {

            @Override
            public void processEvent(final PInputEvent evt, final int i) {
                if (evt.isMouseWheelEvent()) {

//                    double currentScale = camera.getScale();
//                    double scaleDelta = (1.0D + (0.001D * evt.getWheelRotation()));
//
//                    double newScale = currentScale * scaleDelta;
//
//                    if (newScale < MIN_SCALE) {
//                        camera.setViewScale(MIN_SCALE);
//                        return;
//                    }
//                    if ((MAX_SCALE > 0) && (newScale > MAX_SCALE)) {
//                        camera.setViewScale(MAX_SCALE);
//                        return;
//                    }
//                    Point2D pos = evt.getPosition();
//                    camera.scaleViewAboutPoint(scaleDelta, pos.getX(), pos.getY());
//                    
                    final double s = 1D - 0.10D * evt.getWheelRotation();
                    final Point2D p = evt.getPosition();
                    evt.getCamera().scaleViewAboutPoint(s, p.getX(), p.getY());

                    if (evt.getWheelRotation() > 0) {
                        for (TreeNode t : selectedNodes) {
                            t.resetShowAlways();
                        }
                        selectedNodes.clear();
                    }
                }

            }
        });
//
        setAnimatingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        setInteractingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        setDefaultRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);

        mapLegend.setBounds(getWidth() - mapLegend.getWidth() - 10, 20, 300, 45);
        text.setBounds(20, getHeight() - 20 - 20, 600, 20);
        box.setBounds(20, getHeight() - 20 - 20 - 30, 600, 20);
        slider.setBounds(20, 20, 300, 40);

        camera.scaleView(300 / rootNode.getBounds().height);

        this.repaint();

    }

//    public JComboBox getBox() {
//        return box;
//    }
    public Normalizer getSelectedNormalizer(long min, long max) {

        // 
        if (box.getSelectedItem() == "Log Normalization (Siblings)") {
            return new LogNormalizer(min, max);

        }
        if (box.getSelectedItem() == "Square-Root Normalization (Siblings)") {
            return new SquareRootNormalizer(min, max);

        }

        if (box.getSelectedItem() == "Square-Root Normalization (Total Max)") {
            return new SquareRootNormalizer(0, this.maxtotal + 1);

        }

        if (box.getSelectedItem() == "Slider Linear Normalization") {
            return new LinearNormalizer(0, this.maxtotal * this.slider.getValue() / 100.0);

        }
        if (box.getSelectedItem() == "Slider Square-Root Normalization") {
            return new SquareRootNormalizer(0, this.maxtotal * this.slider.getValue() / 100.0);

        }
        if (box.getSelectedItem() == "Slider Log Normalization") {
            return new LogNormalizer(0, this.maxtotal * this.slider.getValue() / 100.0);

        }

        // default log max normalization "Log Normalization (Total Max)"
        return new LogNormalizer(0, this.maxtotal + 1);

    }

    private TreeNode getOrCreate(TreeNode parent, String lookup) {

        TreeNode node;

        if (parents.containsKey(lookup)) {
            node = parents.get(lookup);

        } else {

            node = new TreeNode(this, lookup);

            parents.put(lookup, node);
            parent.addChild(node);
        }

        return node;
    }

    private void createParentNodes(String label, TreeNode node) {

        String tokens[] = label.split("\\.");

        String t[] = Arrays.copyOfRange(tokens, 0, 1);
        String lookup = StringUtils.join(t, ".");
        TreeNode a = getOrCreate(rootNode, lookup);

        t = Arrays.copyOfRange(tokens, 0, 2);
        lookup = StringUtils.join(t, ".");
        TreeNode b = getOrCreate(a, lookup);

        t = Arrays.copyOfRange(tokens, 0, 3);
        lookup = StringUtils.join(t, ".");
        TreeNode c = getOrCreate(b, lookup);

        c.addChild(node);

    }

    public void loadData(Memory memory) {

        for (Map.Entry<String, Clock> entry : memory.getClocks().entrySet()) {

            long total = entry.getValue().getTotal();
            maxtotal += total;
        }

        rootNode = new TreeNode(this, "ClockMap");

        Normalizer normalizer = new LogNormalizer(0, maxtotal);

        for (Map.Entry<String, Clock> entry : memory.getClocks().entrySet()) {

            String label = entry.getKey();

            long total = entry.getValue().getTotal();

            TreeNode node = new TreeNode(this, label, entry.getValue().getValues(), normalizer.normalize(total) * 30, entry.getValue().getMax());

            createParentNodes(label, node);

        }

        rootNode.layout();
        this.initialize();

    }

    public void addSubnet() {
        TreeNode t = new TreeNode(this, "NEW NODE " + counter++);
        rootNode.addChild(t);
        rootNode.layout();
    }

    public TreeNode getNode(String ip) {
        return hash.get(ip);
    }

    private void createMapTypeButtons() {

        mapLegend.setColormap(Main.mainProperties.getColormap());
        mapLegend.setTitle(Main.mainProperties.getTitle());
        final CircularVisualization vis = this;
        box.setOpaque(true);
        text.setOpaque(true);
        text.setText("Search...");
        text.setBackground(Main.mainProperties.getTextBackground());
        text.setForeground(Main.mainProperties.getText());
        box.addItem("Log Normalization (Total Max)");
        box.addItem("Log Normalization (Siblings)");
        box.addItem("Square-Root Normalization (Siblings)");
        box.addItem("Square-Root Normalization (Total Max)");
        box.addItem("Square-Root Normalization (1000)");
        box.addItem("Log Normalization (1000)");
        box.addItem("Slider Linear Normalization");
        box.addItem("Slider Square-Root Normalization");
        box.addItem("Slider Log Normalization");
        //box.setSelected(true);
        box.setVisible(true);
        box.setForeground(Main.mainProperties.getText());
        box.setBackground(Main.mainProperties.getTextBackground());

        box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vis.repaint();
                if (box.getSelectedItem().toString().toLowerCase().contains("slider")) {
                    slider.setVisible(true);
                } else {
                    slider.setVisible(false);
                }
            }
        });

        slider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                vis.repaint();
            }

        });
        slider.setMinimum(0);

        LinearNormalizer maxNormalizer = new LinearNormalizer(0, this.maxtotal + 1);

        int maxSlider = (int) maxNormalizer.normalize(this.maxtotal + 1) * 100;

        slider.setMaximum(maxSlider);
        slider.setOpaque(false);
        slider.setVisible(false);

        text.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                if (text.getText().equalsIgnoreCase("Search...")) {
                    text.setText("");
                }

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (text.getText().length() == 0) {
                    text.setText("Search...");
                }

            }
        });

        text.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                vis.repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        addComponentListener(new ComponentListener() {

            @Override
            public void componentShown(ComponentEvent arg0) {
            }

            @Override
            public void componentResized(ComponentEvent arg0) {
                mapLegend.setBounds(getWidth() - mapLegend.getWidth() - 10, 20, 300, 45);
                text.setBounds(20, getHeight() - 20 - 20, 200, 20);
                box.setBounds(20, getHeight() - 20 - 20 - 30, 200, 20);

            }

            @Override
            public void componentMoved(ComponentEvent arg0) {
            }

            @Override
            public void componentHidden(ComponentEvent arg0) {
            }
        });

        this.add(mapLegend);
        this.add(text);
        this.add(box);
        this.add(slider);

    }

    public String getSearchString() {
        return text.getText();
    }

    public void reposition(int height) {
        camera.scaleView(height / rootNode.getBounds().height);
    }
}
