package org.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
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

        CompilationUnit file = (CompilationUnit) n.getParentNode().get();

        if (!includingPackage || !file.getPackageDeclaration().isPresent()) {
            return n.getNameAsString();
        }

        return file.getPackageDeclaration().get().getName().asString() + "." + n.getNameAsString();
    }

    private String getFullyQualifiedName(MethodDeclaration n, boolean includingParameterName) {

        CompilationUnit compilationUnit = (CompilationUnit) n.getParentNode().get().getParentNode().get();

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
        sb.append(n.getName());
        sb.append("(");

        boolean firstParam = true;
        Iterator var6 = n.getParameters().iterator();

        while(var6.hasNext()) {
            Parameter param = (Parameter)var6.next();

            if (param.getType().asString().equals("FSCsChild")) {
                int debug = 1;
            }
            // compilationUnit.getImport(0).getName().asString()

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

    private void listClasses(String path, File file) throws IOException {
        println("* Class:");
        println(Strings.repeat("=", path.length()));

        try {
            new VoidVisitorAdapter<>() {
                @Override
                public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                    super.visit(n, arg);
                    try {
                        println(getFullyQualifiedName(true, n));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }.visit(StaticJavaParser.parse(file), null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        println(Strings.repeat("=", path.length()));
    }

    private void listMethods(String path, File file) throws IOException {
        println("");
        println("* Method:");
        println(Strings.repeat("=", path.length()));

        try {
            new VoidVisitorAdapter<>() {
                @Override
                public void visit(MethodDeclaration n, Object arg) {
                    super.visit(n, arg);
                    try {
                        println(getFullyQualifiedName(n, false));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }.visit(StaticJavaParser.parse(file), null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        println(Strings.repeat("=", path.length()));
        println("");
        println("");
    }

    private void listFields(String path, File file) throws IOException {
        println("");
        println("* Field:");
        println(Strings.repeat("=", path.length()));

        try {
            new VoidVisitorAdapter<>() {
                @Override
                public void visit(FieldDeclaration n, Object arg) {
                    super.visit(n, arg);
                    try {
                        println(String.valueOf(n.asFieldDeclaration()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }.visit(StaticJavaParser.parse(file), null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        println(Strings.repeat("=", path.length()));
    }

    public void explore(String path, File file) throws IOException {
        outputWriter.write("File: " + path.substring(1));
        println(Strings.repeat("-", path.length() * 2));
        listClasses(path, file);
        listFields(path, file);
        listMethods(path, file);
    }

    private void println(String content) throws IOException {
        outputWriter.write("\n");
        outputWriter.write(content);
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        outputWriter.close();
    }
}
