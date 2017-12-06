package de.uos.inf.swe.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @goal counter
 * @phase test
 */
public class CodeCounter extends AbstractMojo {

    /**
     * Source directory
     *
     * @parameter property="project.build.sourceDirectory"
     * @readonly
     * @required
     */
    private final File sourceDirectory = new File("");

    /**
     * Class directory
     *
     * @parameter property="project.build.outputDirectory"
     * @readonly
     * @required
     */
    private final File outputDirectory = new File("");

    private ArrayList<String> sourceFiles = new ArrayList<String>();

    private int classCount = 0;

    private int linesOfCode = 0;

    private int emptyLines = 0;


    private void getSourceFiles(File root) {
        if (root.isFile()) {
            return;
        }

        try {
            for (File file : root.listFiles()) {
                if (file.isDirectory()) {
                    getSourceFiles(file);
                } else {
                    sourceFiles.add(file.getPath());
                }
            }
        } catch (NullPointerException e) {
            getLog().error(e.getMessage());
        }
    }

    private void countClasses(File root) {
        if (root.isFile()) {
            return;
        }

        try {
            for (File file : root.listFiles()) {
                if (file.isDirectory()) {
                    countClasses(file);
                } else if (file.getPath().contains(".class")) {
                    classCount++;
                }
            }
        } catch (NullPointerException e) {
            getLog().error(e.getMessage());
        }
    }

    private void countLines(String path) {
        try {
            Scanner scan = new Scanner(new File(path));
            String line = null;

            while (scan.hasNext()) {
                line = scan.nextLine();
                if (line.isEmpty()) {
                    emptyLines++;
                } else
                    linesOfCode++;
            }
        } catch (IOException e) {
            getLog().error(e.getMessage());
        }
    }

    public void execute() throws MojoExecutionException {
        if (!sourceDirectory.exists()) {
            getLog().error("Source directory \"" + sourceDirectory + "\" doesn't exist");
        } else {
            getSourceFiles(sourceDirectory);
            for (String path : sourceFiles) {
                countLines(path);
            }
        }

        if (!outputDirectory.exists()) {
            getLog().error("Class directory \"" + outputDirectory + "\" doesn't exist");
        } else
            countClasses(outputDirectory);

        getLog().info("Lines of Code: " + linesOfCode);
        getLog().info("Amount of Class Files: " + classCount);
    }
}
