import classes.child.FSCsChild;
import interfaces.Comparable;

public class Test implements Comparable<Test> {
    private int value;

    public Test(int value) {
        this.value = value;
    }

    @Override
    public int compareTo(Test o) {
        if (this.value == o.value) {
            return 0;
        }
        if (this.value < o.value) {
            return -1;
        }
        return 1;
    }

    public static void testParameter(FSCsChild value) {
        value.abstractMethod2();
    }

    public static void main(String[] args) {
        FSCsChild entity = new FSCsChild();
        System.out.println(entity.getField2());
        testParameter(entity);
    }
}
