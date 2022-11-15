package classes.parent;

public abstract class First {
    protected long field1;
    protected int field2;
    protected double field3;

    public long getField1() {
        return field1;
    }

    public int getField2() {
        return field2;
    }

    public double getField3() {
        return field3;
    }

    public void setField(int field1, int field2) {
        this.field1 = field1;
        this.field2 = field2;
    }

    public void setField(int field2, double field3) {
        this.field3 = field3;
        this.field2 = field2;
    }

    public abstract void printToConsole(String content);

    public abstract void abstractMethod2();
}