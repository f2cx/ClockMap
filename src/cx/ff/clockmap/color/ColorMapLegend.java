package cx.ff.clockmap.color;

import cx.ff.clockmap.Main;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public class ColorMapLegend extends JPanel {

    private ColorMap c;
    final private String minString;
    final private String maxString;
    private String title = "";

    private final Font fontText = new Font("sansserif", Font.PLAIN, 10);
    private final Font fontTitle = new Font("sansserif", Font.BOLD, 12);

    public ColorMapLegend() {
        setLayout(null);

        minString = "  " + Main.mainProperties.getMinLegend();
        maxString = Main.mainProperties.getMaxLegend() + "  ";

        this.setBackground(Main.mainProperties.getLegendBackground());

    }

    public void setColormap(ColorMap c) {
        this.c = c;
        this.repaint();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (c != null) {
            double COLOR_WIDTH = (super.getWidth() - 20) / (double) c.getLength();
            double IMAGE_HEIGHT = super.getHeight() - 24;

            Graphics2D graphics = (Graphics2D) g;
            Color[] colors = c.getColormap();
            Rectangle2D rect = new Rectangle2D.Double();

            g.setFont(fontTitle);
            FontMetrics m = this.getFontMetrics(fontTitle);
            int w = m.stringWidth(title);
            graphics.setColor(Main.mainProperties.getLegendText());
            graphics.drawString(title, super.getWidth() / 2 - w / 2, super.getHeight() - 5);

            for (int i = 0; i < colors.length; i++) {

                rect.setFrame((double) i * COLOR_WIDTH + 10, 0, COLOR_WIDTH, IMAGE_HEIGHT);
                graphics.setColor(colors[i]);
                graphics.fill(rect);
            }

            g.setFont(fontText);
            m = this.getFontMetrics(fontText);

            graphics.setColor(Main.mainProperties.getLegendText());
            graphics.drawString(minString, 0, super.getHeight() - 5);

            w = m.stringWidth(maxString);
            graphics.setColor(Main.mainProperties.getLegendText());
            graphics.drawString(maxString, super.getWidth() - w, super.getHeight() - 5);

        }

    }
}
