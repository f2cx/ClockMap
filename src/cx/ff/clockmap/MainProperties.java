package cx.ff.clockmap;

import cx.ff.clockmap.color.ColorMap;
import cx.ff.clockmap.color.ColorMapFactory;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainProperties implements Serializable {

    private Color background;
    private Color text;
    private Color textBackground;
    private Color clockGroupBackground;
    private Color clockBackground;

    private Color lines;
    private Color selected;
    private String title;
    private String minLegend;
    private String maxLegend;
    private ColorMap colormap;

    private Color outerBackground;

    private Color legendText;
    private Color legendBackground;

    private String tooltipSubnetText;
    private String tooltipHostText;
    private String tooltipHourText;

    private boolean GLOBAL_LINEAR_SCALE;

    private Color string2color(String s) {
        String[] c = s.split(",");
        return new Color(Integer.parseInt(c[0]), Integer.parseInt(c[1]), Integer.parseInt(c[2]), Integer.parseInt(c[3]));
    }

    private boolean string2boolean(String s) {
        if (s.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }

    }

    public MainProperties(File file) {
        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream(file));

            this.background = string2color(prop.get("background").toString());
            this.text = string2color(prop.get("text").toString());
            this.textBackground = string2color(prop.get("textBackground").toString());
            this.clockGroupBackground = string2color(prop.get("clockGroupBackground").toString());
            this.clockBackground = string2color(prop.get("clockBackground").toString());

            this.lines = string2color(prop.get("lines").toString());
            this.selected = string2color(prop.get("selected").toString());
            this.title = prop.get("title").toString();
            this.minLegend = prop.get("minLegend").toString();
            this.maxLegend = prop.get("maxLegend").toString();
            this.colormap = ColorMapFactory.byName(prop.get("colormap").toString());

            this.outerBackground = string2color(prop.get("outerBackground").toString());

            this.legendText = string2color(prop.get("legendText").toString());
            this.legendBackground = string2color(prop.get("legendBackground").toString());

            this.tooltipSubnetText = prop.get("tooltipSubnetText").toString();
            this.tooltipHostText = prop.get("tooltipHostText").toString();
            this.tooltipHourText = prop.get("tooltipHourText").toString();

            this.GLOBAL_LINEAR_SCALE = string2boolean(prop.get("globalLinearScale").toString());

        } catch (IOException ex) {
            Logger.getLogger(MainProperties.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Color getBackground() {
        return background;
    }

    public Color getText() {
        return text;
    }

    public Color getTextBackground() {
        return textBackground;
    }

    public Color getClockGroupBackground() {
        return clockGroupBackground;
    }

    public Color getClockBackground() {
        return clockBackground;
    }

    public Color getLines() {
        return lines;
    }

    public Color getSelected() {
        return selected;
    }

    public String getTitle() {
        return title;
    }

    public String getMinLegend() {
        return minLegend;
    }

    public String getMaxLegend() {
        return maxLegend;
    }

    public ColorMap getColormap() {
        return colormap;
    }

    public Color getOuterBackground() {
        return outerBackground;
    }

    public Color getLegendText() {
        return legendText;
    }

    public Color getLegendBackground() {
        return legendBackground;
    }

    public String getTooltipSubnetText() {
        return tooltipSubnetText;
    }

    public String getTooltipHostText() {
        return tooltipHostText;
    }

    public String getTooltipHourText() {
        return tooltipHourText;
    }

    public boolean isGLOBAL_LINEAR_SCALE() {
        return GLOBAL_LINEAR_SCALE;
    }

}
