/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Meka.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.minify;

import com.github.fracpete.deps4j.MinDeps;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Minifies a Meka build environment using a specified minimum set of classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Meka {

  /** the java home directory to use. */
  protected File m_JavaHome;

  /** the file with classes to determine the minimum dependencies for. */
  protected File m_ClassesFile;

  /** the file with additional class names to include (optional). */
  protected File m_AdditionalFile;

  /** the input build env. */
  protected File m_Input;

  /** the absolute input path. */
  protected String m_InputAbs;

  /** packages to keep. */
  protected List<String> m_Packages;

  /** the output build env. */
  protected File m_Output;

  /** the absolute output path. */
  protected String m_OutputAbs;

  /** whether to test the build environment. */
  protected boolean m_Test;

  /**
   * Initializes the minifier.
   */
  public Meka() {
    super();

    m_JavaHome       = null;
    m_ClassesFile    = null;
    m_AdditionalFile = null;
    m_Input          = null;
    m_InputAbs       = null;
    m_Packages       = new ArrayList<>();
    m_Output         = null;
    m_OutputAbs      = null;
    m_Test           = false;
  }

  /**
   * Sets the java home directory.
   *
   * @param value	the directory
   */
  public void setJavaHome(File value) {
    m_JavaHome = value;
  }

  /**
   * Returns the java home directory.
   *
   * @return		the directory
   */
  public File getJavaHome() {
    return m_JavaHome;
  }

  /**
   * Sets the file with the class files to inspect.
   *
   * @param value	the file
   */
  public void setClassesFile(File value) {
    m_ClassesFile = value;
  }

  /**
   * Returns the file with the class files to inspect.
   *
   * @return		the file, null if not set
   */
  public File getClassesFile() {
    return m_ClassesFile;
  }

  /**
   * Sets the file with the additional classnames to include (optional).
   *
   * @param value	the file
   */
  public void setAdditionalFile(File value) {
    m_AdditionalFile = value;
  }

  /**
   * Returns the file with the additional classnames to include (optional).
   *
   * @return		the file, null if not set
   */
  public File getAdditionalFile() {
    return m_AdditionalFile;
  }

  /**
   * Sets the directory to use as input build environment.
   *
   * @param value	the directory
   */
  public void setInput(File value) {
    m_Input = value;
    if (m_Input == null)
      m_InputAbs = null;
    else
      m_InputAbs = m_Input.getAbsolutePath();
  }

  /**
   * Returns the directory to use as input build environment.
   *
   * @return		the directory, null if not set
   */
  public File getInput() {
    return m_Input;
  }

  /**
   * Sets the packages to keep.
   *
   * @param value	the packages
   */
  public void setPackages(List<String> value) {
    m_Packages.addAll(value);
  }

  /**
   * Returns the packages to keep.
   *
   * @return		the packages
   */
  public List<String> getPackages() {
    return m_Packages;
  }

  /**
   * Sets the directory for the output build environment.
   *
   * @param value	the directory
   */
  public void setOutput(File value) {
    m_Output = value;
    if (m_Output == null)
      m_OutputAbs = null;
    else
      m_OutputAbs = m_Output.getAbsolutePath();
  }

  /**
   * Returns the directory for output build environment.
   *
   * @return		the directory, null if not set
   */
  public File getOutput() {
    return m_Output;
  }

  /**
   * Sets whether to test the minified build env.
   *
   * @param value	true if to test
   */
  public void setTest(boolean value) {
    m_Test = value;
  }

  /**
   * Returns whether to test the minified build env.
   *
   * @return		true if to test
   */
  public boolean getTest() {
    return m_Test;
  }

  /**
   * Sets the commandline options.
   *
   * @param options	the options to use
   * @return		true if successful
   * @throws Exception	in case of an invalid option
   */
  public boolean setOptions(String[] options) throws Exception {
    ArgumentParser parser;
    Namespace ns;

    parser = ArgumentParsers.newArgumentParser(Meka.class.getName());
    parser.addArgument("--java-home")
      .type(Arguments.fileType().verifyExists().verifyIsDirectory())
      .dest("javahome")
      .required(true)
      .help("The java home directory of the JDK that includes the jdeps binary, default is taken from JAVA_HOME environment variable.");
    parser.addArgument("--classes")
      .type(Arguments.fileType().verifyExists().verifyIsFile().verifyCanRead())
      .dest("classes")
      .required(true)
      .help("The file containing the classes to determine the dependencies for. Empty lines and lines starting with # get ignored.");
    parser.addArgument("--additional")
      .type(Arguments.fileType())
      .setDefault(new File("."))
      .required(false)
      .dest("additional")
      .help("The file with additional class names to just include.");
    parser.addArgument("--input")
      .type(Arguments.fileType())
      .setDefault(new File("."))
      .required(true)
      .dest("input")
      .help("The directory with the pristing build environment in.");
    parser.addArgument("--output")
      .type(Arguments.fileType().verifyIsDirectory().verifyExists())
      .setDefault(new File("."))
      .required(true)
      .dest("output")
      .help("The directory for storing the minified build environment in.");
    parser.addArgument("--test")
      .action(Arguments.storeTrue())
      .required(false)
      .dest("test")
      .help("Optional testing of the minified build environment.");
    parser.addArgument("package")
      .dest("packages")
      .required(true)
      .nargs("+")
      .help("The packages to keep, eg 'meka'.");

    try {
      ns = parser.parseArgs(options);
    }
    catch (ArgumentParserException e) {
      parser.handleError(e);
      return false;
    }

    setJavaHome(ns.get("javahome"));
    setClassesFile(ns.get("classes"));
    setAdditionalFile(ns.get("additional"));
    setInput(ns.get("input"));
    setPackages(ns.getList("packages"));
    setOutput(ns.get("output"));
    setTest( ns.getBoolean("test"));

    return true;
  }

  /**
   * Performs some checks.
   *
   * @return		null if successful, otherwise error message
   */
  protected String check() {
    if (!m_JavaHome.exists())
      return "Java home directory does not exist: " + m_JavaHome;
    if (!m_JavaHome.isDirectory())
      return "Java home does not point to a directory: " + m_JavaHome;

    if (!m_ClassesFile.exists())
      return "File with class names does not exist: " + m_ClassesFile;
    if (m_ClassesFile.isDirectory())
      return "File with class names points to directory: " + m_ClassesFile;

    if (!m_Input.exists())
      return "Input build environment does not exist: " + m_Input;
    if (!m_Input.isDirectory())
      return "Input build environment points to a file: " + m_Input;

    if (m_Output == null)
      return "No output directory supplied!";

    return null;
  }

  /**
   * Builds the specified Meka environment.
   *
   * @param dir		the build env
   * @return		null if successful, otherwise error message
   */
  protected String build(File dir) {
    String			error;
    String[] 			cmd;
    ProcessBuilder 		builder;
    CollectingProcessOutput 	output;

    cmd = new String[]{
      "mvn",
      "clean",
      "compile",
      "-DskipTests=True",
    };
    builder = new ProcessBuilder();
    builder.command(cmd);
    builder.directory(dir);
    output = new CollectingProcessOutput();
    try {
      output.monitor(builder);
      if (!output.hasSucceeded()) {
        error = "\nExit code: " + output.getExitCode();
        if (output.getStdErr().length() > 0)
          error += "\nStderr:\n" + output.getStdErr();
        if (output.getStdOut().length() > 0)
          error += "\nStdout:\n" + output.getStdOut();
        return error;
      }
    }
    catch (Exception e) {
      return "Failed to execute: " + builder.toString() + "\n" + e;
    }

    return null;
  }

  /**
   * Determines the classes to keep.
   *
   * @param classes	to fill in the classes
   * @return		null if successful, otherwise error message
   */
  protected String determineClasses(List<String> classes) {
    MinDeps	min;
    String	msg;

    // determine minimum set of classes
    min = new MinDeps();
    min.setJavaHome(getJavaHome());
    min.setPackages(new ArrayList<>(m_Packages));
    // TODO load pom.xml and analyze dependencies, then use .m2/repository 
    // to build class path
    min.setClassPath("");
    min.setClassesFile(m_ClassesFile);
    min.setAdditionalFile(m_AdditionalFile);
    msg = min.execute();
    if (msg != null)
      return "Failed to execute " + MinDeps.class.getName() + ": " + msg;

    classes.addAll(min.getDependencies());

    return null;
  }

  /**
   * Prepares the output directory, either creating or emptying it.
   *
   * @return		null if successful, otherwise error message
   */
  protected String prepareOutputDir() {
    File[]	files;
    String	msg;
    File	libDir;

    files = m_Output.listFiles();
    if (files == null) {
      System.err.println("Creating output dir...");
      if (m_Output.mkdirs())
        return "Failed to create output directory: " + m_Output;
    }
    else {
      if (files.length > 0) {
	System.err.println("Cleaning output dir...");
	for (File file: files) {
	  if (file.getName().equals("") || file.getName().equals(".."))
	    continue;
	  if (file.isDirectory()) {
	    try {
	      FileUtils.deleteDirectory(file);
	    }
	    catch (Exception e) {
	      return "Failed to delete directory: " + file + "\n" + e;
	    }
	  }
	  else {
	    if (!file.delete())
	      return "Failed to delete file: " + file;
	  }
	}
      }
    }

    // pom.xml
    msg = copyFile(new File(m_InputAbs + File.separator + "pom.xml"));
    if (msg != null)
      return msg;


    return null;
  }

  /**
   * Copies the specified input file into the output directory.
   *
   * @param inputFile	the file to copy
   * @return		null if successful, otherwise error message
   */
  protected String copyFile(File inputFile) {
    File	outputFile;
    String	subPath;

    if (inputFile.exists()) {
      subPath = inputFile.getAbsolutePath().substring(m_InputAbs.length());
      outputFile = new File(m_OutputAbs + File.separator + subPath);
      try {
	FileUtils.copyFile(inputFile, outputFile);
      }
      catch (Exception e) {
	return "Failed to copy file: " + inputFile + " -> " + outputFile + "\n" + e;
      }
    }
    else {
      System.err.println("Missing: " + inputFile);
    }

    return null;
  }

  /**
   * Generates a class file name from the class name.
   *
   * @param cls		the class to convert
   * @return		the filename
   */
  protected File classToFile(String cls) {
    return new File(
      m_InputAbs
	+ File.separator + "src" + File.separator + "main" + File.separator + "java"
	+ File.separator + cls.replace(".", File.separator) + ".java");
  }

  /**
   * Copies the classes and resources across.
   *
   * @param classes	the classes to copy
   * @return		null if successful, otherwise error message
   */
  protected String copy(List<String> classes) {
    List<File> 		inputDirs;
    File[]		files;
    File		inFile;
    File		inDir;
    String		msg;

    // classes
    inputDirs = new ArrayList<>();
    for (String cls: classes) {
      inFile = classToFile(cls);
      // record directories
      inDir = inFile.getParentFile();
      if (!inputDirs.contains(inDir))
        inputDirs.add(inDir);
      // copy
      msg = copyFile(inFile);
      if (msg != null)
        return msg;
    }

    // other resources
    System.err.println("Copying resources...");
    // TODO change to "main/resources"
    for (File inputDir : inputDirs) {
      files = inputDir.listFiles((File dir, String name) -> {
	  return !name.equals(".") && !name.equals("..") && !name.endsWith(".java");
      });
      if (files != null) {
        System.err.println("- " + inputDir);
	for (File file: files) {
	  if (file.isDirectory())
	    continue;
	  msg = copyFile(file);
	  if (msg != null)
	    return msg;
	}
      }
    }

    return null;
  }

  /**
   * Minifies the build environment.
   *
   * @return		null if successful, otherwise error message
   */
  protected String minify() {
    String		msg;
    List<String>	classes;

    // minimal set of classes
    System.err.println("Determining minimal set of classes...");
    classes = new ArrayList<>();
    msg     = determineClasses(classes);
    if (msg != null)
      return msg;

    // prepare the output directory
    msg = prepareOutputDir();
    if (msg != null)
      return msg;

    // copy the classes/resources across
    msg = copy(classes);
    if (msg != null)
      return msg;

    return null;
  }

  /**
   * Determines the dependencies.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute() {
    String		result;

    result = check();

    if (result == null) {
      result = build(m_Input);
      if (result != null)
        result = "Failed to build input build environment: " + result;
    }

    if (result == null)
      result = minify();

    if (result == null) {
      if (m_Test) {
	result = build(m_Output);
	if (result != null)
	  result = "Failed to build minified build environment: " + result;
      }
    }

    return result;
  }

  public static void main(String[] args) throws Exception {
    Meka 	meka;
    String	error;

    meka = new Meka();
    if (meka.setOptions(args)) {
      error = meka.execute();
      if (error != null) {
	System.err.println(error);
	System.exit(2);
      }
    }
    else {
      System.exit(1);
    }
  }
}
