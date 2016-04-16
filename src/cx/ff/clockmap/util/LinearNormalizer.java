package cx.ff.clockmap.util;

public class LinearNormalizer extends Normalizer {

    public LinearNormalizer(double minValue, double maxValue) {
        super(minValue, maxValue);
    }

    @Override
    public double normalize(double value) {
        return (value - this.normMinValue) / this.normMaxMinValue;
    }

    @Override
    protected void initNormalizer(double minValue, double maxValue) {
        this.normMinValue = minValue;
        this.normMaxMinValue = maxValue - minValue;
    }

    @Override
    public TYPE getType() {
        return TYPE.LINEAR;
    }

    @Override
    public double invert(double value) {
        return (this.maxValue - this.minValue) * value + this.minValue;
    }
}
