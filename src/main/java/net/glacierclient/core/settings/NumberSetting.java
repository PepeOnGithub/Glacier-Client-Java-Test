package net.glacierclient.core.settings;

public class NumberSetting extends Setting<Double> {

    private final double min;
    private final double max;
    private final double increment;

    public NumberSetting(String name, String description, double min, double max, double defaultValue) {
        this(name, description, min, max, defaultValue, 0.1);
    }

    public NumberSetting(String name, double defaultValue, double min, double max) {
        this(name, "", min, max, defaultValue, 0.1);
    }

    public NumberSetting(String name, String description, double min, double max, double defaultValue, double increment) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    @Override
    public void setValue(Double value) {
        super.setValue(Math.max(min, Math.min(max, value)));
    }

    public void increment() { setValue(value + increment); }
    public void decrement() { setValue(value - increment); }

    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getIncrement() { return increment; }
    public float getPercent() { return (float) ((value - min) / (max - min)); }
    public int getValueAsInt() { return value.intValue(); }
    public double get() { return value; }

    @Override
    public String getTypeName() { return "number"; }
}
