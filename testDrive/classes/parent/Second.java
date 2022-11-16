package classes.parent;

public class Second {
    private int privateField1;
    public static String publicField1;
    public String field2;
    public final int PUBLIC_FIELD_3 = 100;

    public static class nestedClass1 {
        private int field1 = 0;
        private int field2 = 0;

        public nestedClass1(int field1, int field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        public void nestedMethod1() {
            System.out.println("This is nestedClass's method in Second.");
        }

        public static class nestedClass2 {
            private int field3 = 0;
            private int field4 = 0;

            public nestedClass2(int field3, int field4) {
                this.field3 = field3;
                this.field4 = field4;
            }

            public void nestedMethod2() {
                System.out.println("This is nestedClass's method in Second.");
            }
        }
    }

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