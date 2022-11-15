package classes.child;

import classes.parent.First;

public class FirstsFirstChild extends First {
    @Override
    public void printToConsole(String content) {
        System.out.println("This is FirstsFirstChild");
    }

    @Override
    public void abstractMethod2() {

    }
}