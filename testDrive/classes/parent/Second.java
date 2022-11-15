package classes.parent;

public class Second {
    private int privateField1;
    public static String publicField1;
    public String field2;
    public final int PUBLIC_FIELD_3 = 100;

    public int getPrivateField1() {
        return privateField1;
    }

    public void setPrivateField1(int privateField1) {
        this.privateField1 = privateField1;
    }

    public static String getPublicField1() {
        return publicField1;
    }

    public static void setPublicField1(String publicField1) {
        Second.publicField1 = publicField1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public static void method1() {
        System.out.println("This is Second.method1()");
    }

    public boolean method2(String param1) {
        return field2.equals(param1);
    }
}