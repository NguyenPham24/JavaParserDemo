package classes.child;

import classes.parent.First;

public abstract class FirstsSecondChild extends First {
    @Override
    public void printToConsole(String content) {
        System.out.println("This is FirstsSecondChild");
    }
}