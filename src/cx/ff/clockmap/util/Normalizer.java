package cx.ff.clockmap.util;

import java.util.HashMap;
import java.util.Map;

public abstract class Normalizer {

    public enum TYPE {

        /**
         * square root normalizer
         */
        SQUARE_ROOT("square root normalization"),
        /**
         * logarithmic normalizer
         */
        LOGARITHMIC("logarithmic normalization"),
        /**
         * linear normalizer
         */
        LINEAR("no normalization");
        public final String name;

        private TYPE(String name) {
            this.name = name;
        }

        public static TYPE byName(String name) {
            for (TYPE t : TYPE.values()) {
                if (t.name.equals(name)) {
                    return t;
                }
            }

            return LINEAR;
        }
    }
    protected double maxValue;
    protected double minValue;
    protected double normMinValue;
    protected double normMaxMinValue;
    protected Map<Double, Double> cache = new HashMap<Double, Double>(200);

    protected Normalizer(double minValue, double maxValue) {
        this.minValue = 0;
        this.maxValue = maxValue;
        this.initNormalizer(this.minValue, this.maxValue);
    }

    public abstract double normalize(double value);

    protected abstract void initNormalizer(double minValue, double maxValue);

    public abstract double invert(double value);

    public abstract TYPE getType();

    public static Normalizer getNormalizer(TYPE type, double min, double max) {
        switch (type) {
            case LINEAR:
                return new LinearNormalizer(min, max);
            case SQUARE_ROOT:
                return new SquareRootNormalizer(min, max);
            case LOGARITHMIC:
                return new LogNormalizer(min, max);
            default:
                throw new IllegalArgumentException("unhandled normalizer: " + type.toString());
        }
    }
}
