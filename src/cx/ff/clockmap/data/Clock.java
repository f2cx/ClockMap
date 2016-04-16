package cx.ff.clockmap.data;

import java.io.Serializable;

public class Clock implements Serializable {

    long values[] = new long[24];
    long total = 0;
    long max = 0;

    public Clock() {
        for (int i = 0; i < 24; i++) {
            values[i] = 0;
        }
    }

    public long getMax() {
        return max;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long[] getValues() {
        return values;
    }

    public void setValues(long[] values) {
        this.values = values;
    }

    synchronized public void addValue(int hour, long value) {
        values[hour] += value;
        total += value;

        if (value > max) {
            max = value;
        }
    }
}
