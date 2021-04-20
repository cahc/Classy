package LibLinearMultiLabel.cc.fork;

class MutableDouble {

    private boolean initialized;
    private double  value;

    public MutableDouble() {
    }

    public MutableDouble(double value) {
        set(value);
    }

    void set(double value) {
        this.value = value;
        this.initialized = true;
    }

    double get() {
        if (!initialized) {
            throw new IllegalStateException("Value not yet initialized");
        }
        return value;
    }

}
