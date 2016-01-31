package org.lucci.lmu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
		if (extension.equals("dot")
				|| extension.equals("ps")
				|| extension.equals("png")
				|| extension.equals("fig")
				|| extension.equals("svg")) {
			return new File(output);
		}
		
		throw new IOException("Bad output file extension");
	}

	public static void main(String[] args) {
		Model diagram = new Model();
		String inputFileName = args[0];
		String outputFileName = args[1];
		File input;
		File output;
		
		try {
			// Check input
			input = checkInput(inputFileName);
			
			// Check output
			output = checkOutput(outputFileName);
			
			Analyzer analyzer = (Analyzer) Analyzer.getModelFactory("analyzer");
			
			
			switch(extension){
				case "java" :
					break;
				case "jar" :
						diagram = analyzer.jarAnalysis(inputFileName);
						break;
				default :
					break;					
			}
					
			// Export Model
			export(diagram, output);
			
			System.out.println("Done");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
