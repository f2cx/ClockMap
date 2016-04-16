package cx.ff.clockmap.color;

import java.awt.Color;

public class PredefinedMap implements ColorMap {

    private final Color[] map;

    public PredefinedMap(Color[] colors) {
        this.map = colors;
    }

    @Override
    public Color[] getColormap() {
        return this.map;
    }

    @Override
    public Color getColorAt(int n) throws IndexOutOfBoundsException {
        return this.map[n];
    }

    @Override
    public int getLength() {
        return map.length;
    }

    @Override
    public ColorMap getMap(int steps) {
        return this;
    }
}
