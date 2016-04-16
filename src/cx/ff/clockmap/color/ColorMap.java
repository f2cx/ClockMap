package cx.ff.clockmap.color;

import java.awt.Color;

public interface ColorMap {

    public Color[] getColormap();

    public Color getColorAt(int n) throws IndexOutOfBoundsException;

    public int getLength();

    public ColorMap getMap(int steps);
}
