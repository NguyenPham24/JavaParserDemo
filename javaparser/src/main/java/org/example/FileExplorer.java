package org.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Strings;

import java.io.*;
import java.util.Iterator;

public class FileExplorer implements Closeable {
    private final OutputStreamWriter outputWriter;

    public FileExplorer(String outputPath) throws IOException {
        outputWriter = new FileWriter(outputPath);
    }

    private String getFullyQualifiedName(boolean includingPackage, ClassOrInterfaceDeclaration n) {

        CompilationUnit file = getCompilationUnit(n);

        if (!includingPackage || !file.getPackageDeclaration().isPresent()) {
            return n.getNameAsString();
        }

        return file.getPackageDeclaration().get().getName().asString() + "." + n.getNameAsString();
    }

    private String getFullyQualifiedName(boolean includingPackage, MethodDeclaration n, boolean includingParameterName) {

        CompilationUnit compilationUnit = getCompilationUnit(n);

        StringBuilder sb = new StringBuilder();

        if (n.getModifiers().isNonEmpty()) {
            AccessSpecifier accessSpecifier = n.getAccessSpecifier();
            sb.append(accessSpecifier.asString()).append(" ");
            if (n.isStatic()) {
                sb.append("static ");
            }

            if (n.isAbstract()) {
                sb.append("abstract ");
            }

            if (n.isFinal()) {
                sb.append("final ");
            }

            if (n.isNative()) {
                sb.append("native ");
            }

            if (n.isSynchronized()) {
                sb.append("synchronized ");
            }
        }
        sb.append(n.getType().toString());
        sb.append(" ");
        if (includingPackage) {
            sb.append(compilationUnit.getPackageDeclaration().get().getNameAsString());
            sb.append(".");
        }
        sb.append(n.getName());
        sb.append("(");

        boolean firstParam = true;
        Iterator var6 = n.getParameters().iterator();

        while(var6.hasNext()) {
            Parameter param = (Parameter)var6.next();

            if (firstParam) {
                firstParam = false;
            } else {
                sb.append(", ");
            }

            if (includingParameterName) {
                sb.append(param);
            } else {
                sb.append(getFullyQualifiedName(param, compilationUnit));
                if (param.isVarArgs()) {
                    sb.append("...");
                }
            }
        }
        sb.append(") ");

        return sb.toString();
    }

    private String getFullyQualifiedName(Parameter param, CompilationUnit unit) {
        StringBuilder sb = new StringBuilder();

        /// Find the fully qualified name from the imports if any
        String paramType = param.getTypeAsString();
        for (int i = 0; i < unit.getImports().size(); ++i) {
            String importStm = unit.getImport(i).getName().asString();
            if (importStm.endsWith(paramType)) {
                sb.append(importStm);
                break;
            }
        }
        if (sb.toString().equals("")) {
            sb.append(paramType);
        }

        return sb.toString();
    }

    private static CompilationUnit getCompilationUnit(Node n1) {
        while (!(n1 instanceof CompilationUnit)) {
            if(n1.getParentNode().isPresent()) {
                n1 = n1.getParentNode().get();
            } else return null;
        }
        return (CompilationUnit)n1;
    }

    private void listClasses(String path, File file) {
        try {
            new VoidVisitorAdapter<>() {
                @Override
                public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                    try {
                        if (!n.getParentNode().get().equals(StaticJavaParser.parse(file))) {
                            return;
                        }
                        super.visit(n, arg);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        String classOrInterface = (n.isInterface() ? "*** Interface ***" : "*** Class ***");
                        println(classOrInterface);
                        println(Strings.repeat("=", path.length()));
                        println(getFullyQualifiedName(true, n));
                        println(Strings.repeat("=", path.length()));
                        listFields(n);
                        listConstructors(n);
                        listMethods(n);
                        exploreNestedClasses(n);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }.visit(StaticJavaParser.parse(file), null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void exploreNestedClasses(ClassOrInterfaceDeclaration parentDecl) {
        VoidVisitorAdapter<Object> visitor = new VoidVisitorAdapter<>() {
            @Override
            public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                if (!n.getParentNode().get().equals(parentDecl)) {
                    return;
                }
                super.visit(n, arg);
                try {
                    String classOrInterface = "*** " + parentDecl.getNameAsString() + "'s ";
                    classOrInterface += (n.isInterface() ? "nested interface" : "nested class");
                    classOrInterface += " ***";
                    println(classOrInterface);
                    println(Strings.repeat("=", 20));
                    println(getFullyQualifiedName(true, n));
                    println(Strings.repeat("=", 20));
                    listFields(n);
                    listConstructors(n);
                    listMethods(n);
                    exploreNestedClasses(n);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        /// Method body starts here.
        parentDecl.getMembers().forEach((p) -> {
            if (p instanceof ClassOrInterfaceDeclaration) {
                visitor.visit((ClassOrInterfaceDeclaration) p, null);
            }
        });
    }

    private void listConstructors(ClassOrInterfaceDeclaration parentDecl) throws IOException {
        println("");
        println("* Constructor:");
        println(Strings.repeat("=", 20));

        new VoidVisitorAdapter<>() {
            @Override
            public void visit(ConstructorDeclaration n, Object arg) {
                if (!n.getParentNode().get().equals(parentDecl)) {
                    return;
                }
                super.visit(n, arg);
                try {
                    println(n.getDeclarationAsString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.visit(parentDecl, null);

        println(Strings.repeat("=", 20));
        println("");
    }

    private void listMethods(ClassOrInterfaceDeclaration parentDecl) throws IOException {
        println("* Method:");
        println(Strings.repeat("=", 20));

        new VoidVisitorAdapter<>() {
            @Override
            public void visit(MethodDeclaration n, Object arg) {
                if (!n.getParentNode().get().equals(parentDecl)) {
                    return;
                }

                super.visit(n, arg);
                try {
                    println(n.getDeclarationAsString(true, true, true));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.visit(parentDecl, null);

        println(Strings.repeat("=", 20));
        println("");
    }

    private void listFields(ClassOrInterfaceDeclaration parentDecl) throws IOException {
        println("");
        println("* Field:");
        println(Strings.repeat("=", 20));

        new VoidVisitorAdapter<>() {
            @Override
            public void visit(FieldDeclaration n, Object arg) {
                if (!n.getParentNode().get().equals(parentDecl)) {
                    return;
                }
                super.visit(n, arg);
                try {
                    println(String.valueOf(n.asFieldDeclaration()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.visit(parentDecl, null);
        println(Strings.repeat("=", 20));
    }

    public void explore(String path, File file) throws IOException {
        println("File: " + path.substring(1));
        println(Strings.repeat("-", path.length() + 7));
        println("");
        listClasses(path, file);
    }

    private void println(String content) throws IOException {
        outputWriter.write(content);
        outputWriter.write("\n");
    }

    @Override
    public void close() throws IOException {
        outputWriter.close();
    }
}
