package cx.ff.clockmap.util;

public class SquareRootNormalizer extends Normalizer {

    private final double invertSqrtMaxMin;
    private final double invertSqrtMin;

    public SquareRootNormalizer(double minValue, double maxValue) {
        super(minValue, maxValue);

        this.invertSqrtMin = Math.sqrt(this.minValue);
        this.invertSqrtMaxMin = Math.sqrt(this.maxValue) - Math.sqrt(this.minValue);
    }

    @Override
    public double normalize(double value) {
        Double result = this.cache.get(value);
        if (result == null) {
            result = (Math.sqrt(value) - this.normMinValue) / this.normMaxMinValue;
            this.cache.put(value, result);
        }

        return result;
    }

    @Override
    protected void initNormalizer(double minValue, double maxValue) {
        this.normMinValue = Math.sqrt(minValue);
        this.normMaxMinValue = Math.sqrt(maxValue) - Math.sqrt(minValue);
    }

    @Override
    public TYPE getType() {
        return TYPE.SQUARE_ROOT;
    }

    @Override
    public double invert(double value) {
        return Math.pow((value * this.invertSqrtMaxMin) + this.invertSqrtMin, 2);
    }
}
