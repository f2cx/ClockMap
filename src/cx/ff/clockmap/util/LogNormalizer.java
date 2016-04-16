package cx.ff.clockmap.util;

public class LogNormalizer extends Normalizer {

    public LogNormalizer(double minValue, double maxValue) {
        super(minValue, maxValue);
    }

    @Override
    public double normalize(double value) {
        Double result = this.cache.get(value);
        if (result == null) {
            result = (Math.log(value + 1) - this.normMinValue) / this.normMaxMinValue;
            this.cache.put(value, result);
        }

        return result;
    }

    @Override
    protected void initNormalizer(double minValue, double maxValue) {
        this.normMinValue = Math.log(minValue + 1);
        this.normMaxMinValue = Math.log(maxValue + 1) - Math.log(minValue + 1);
    }

    @Override
    public TYPE getType() {
        return TYPE.LOGARITHMIC;
    }

    @Override
    public double invert(double value) {
        return (this.minValue + 1)
                * Math.exp(Math.log(this.maxValue + 1)
                * value - Math.log(this.minValue + 1) * value) - 1;
    }
}
