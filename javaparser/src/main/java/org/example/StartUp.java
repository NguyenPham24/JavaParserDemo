package org.example;

import java.io.*;
import java.util.Scanner;

public class StartUp {
    public static void analyse(String outputPath, File projectFolder) throws IOException {
        FileExplorer fileExplorer = new FileExplorer(outputPath);

        new DirIterator(
                (path) -> path.endsWith(".java"),
                fileExplorer
        ).explore(projectFolder);

        fileExplorer.close();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Repo's absolute path: ");
        String repoFolder = scanner.nextLine();

        File projectDir = new File(repoFolder + "/testDrive");
        analyse(repoFolder + "/javaParser/output.txt", projectDir);
    }

//    public static void main(String[] args) throws IOException {
//        FileInputStream inputStream = new FileInputStream("D:/UNIVERSITY/lab/first/javaparser/input.txt");
//        Scanner scanner = new Scanner(inputStream);
//
//        String projectFolderPath = scanner.nextLine();
//        System.out.println("Project folder path: " + projectFolderPath);
//
//        String outputFileName = scanner.nextLine();
//        System.out.println("Output file name: " + outputFileName);
//
//        File projectDir = new File(projectFolderPath);
//        analyse(outputFileName, projectDir);
//    }
}
