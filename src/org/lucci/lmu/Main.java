package org.lucci.lmu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.lucci.lmu.input.ParseError;
import org.lucci.lmu.output.AbstractWriter;
import org.lucci.lmu.output.WriterException;

import analysis.Analyzer;

public class Main {

	/**
	 * Export the diagram into the output file
	 * 
	 * @param diagram
	 * @param f
	 */
	static String extension;

	private static void export(Model diagram, File f) {
		try {
			AbstractWriter factory = AbstractWriter.getTextFactory(FileChooser.getFileExtension(f.getName()));
			FileOutputStream fis = new FileOutputStream(f);
			fis.write(factory.writeModel(diagram));
			fis.flush();
			fis.close();
		} catch (IOException ex) {
			System.out.println("I/O error while writing " + f.getAbsolutePath() + ": " + ex.getMessage());
		} catch (WriterException ex) {
			System.out.println("Error: " + ex.getMessage());
		}

	}

	private static File checkInput(String input) throws IOException {
		extension = FileChooser.getFileExtension(input);
		if (extension.equals("jar")) {
			return new File(input);
		}

		throw new IOException("Bad input file extension");
	}

	private static File checkOutput(String output) throws IOException {
		String extension = FileChooser.getFileExtension(output);
		if (extension.equals("dot") || extension.equals("ps") || extension.equals("png") || extension.equals("fig")
				|| extension.equals("svg")) {
			return new File(output);
		}

		throw new IOException("Bad output file extension");
	}

	private static Model createModel(String inputFileName) throws ParseError {
		Analyzer analyzer = (Analyzer) Analyzer.getModelFactory("analyzer");

		switch (extension) {
		case "java":
			return null;
		case "jar":
			return analyzer.jarAnalysis(inputFileName);
		default:
			return null;
		}
	}

	private static void dependencyAnalysis(String inputFileName) throws IOException {
		JarFile input = new JarFile(inputFileName);
		Manifest manifest = input.getManifest();

		// Check if there is a manifest
		if (manifest != null) {
			System.out.println(manifest.toString());
			final Attributes mattr = manifest.getMainAttributes();
			for (Object key : mattr.keySet()) {
				if (key != null && (key.toString()).contains("Import-Package")) {
					String dependencies = mattr.getValue((Name) key);
					String delims = ";";
					String[] tokens = dependencies.split(delims);
					String[] test = Arrays.copyOf(tokens, tokens.length-1);
					for(int i = 1; i < test.length; i++) {
						String temp = test[i];
						String[] test2 = temp.split(",");
						test[i] = test2[2];
					}
					
					for(String s : test) {
						System.out.println(s);
					}
				}
			}
		} else {
			System.out.println("No Dependencies");
		}
	}

	public static void main(String[] args) {
		Model diagram = new Model();
		String inputFileName = args[0];
		String outputFileName = args[1];
		String mode = args[2];
		File output;

		try {
			// Check input
			checkInput(inputFileName);

			// Check output
			output = checkOutput(outputFileName);

			if (mode.equals("classes")) {
				// Create Model
				diagram = createModel(inputFileName);

				// Export Model
				export(diagram, output);
			} else if (mode.equals("dependencies")) {
				dependencyAnalysis(inputFileName);
			} else {
				throw new Exception("Bad arguments given");
			}

			System.out.println("Done");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
