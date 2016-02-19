package fr.unice.polytech.lmu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.lucci.lmu.FileChooser;
import org.lucci.lmu.Model;
import org.lucci.lmu.input.ParseError;
import org.lucci.lmu.output.AbstractWriter;
import org.lucci.lmu.output.WriterException;

import analysis.Analyzer;

public class Connector {
	Model model;
	static String extension;

	public Connector() {
		model = new Model();
	}

	private static File checkInput(String input) throws IOException {
		extension = FileChooser.getFileExtension(input);
		if (extension.equals("jar")) {
			return new File(input);
		}

		throw new IOException("Bad input file extension");
	}
	
	private static void output(String outputFileName, Model model) throws IOException, WriterException {
		File output = new File(outputFileName);
		AbstractWriter factory = AbstractWriter.getTextFactory(FileChooser.getFileExtension(output.getName()));
		FileOutputStream fis = new FileOutputStream(output);
		fis.write(factory.writeModel(model));
		fis.flush();
		fis.close();
	}

	public void exportJar(String jarLocation, String exportDir, String exportFormat)
			throws ParseError, IOException, WriterException {
		checkInput(jarLocation);
		String outputFileName = exportDir + "." + exportFormat;

		Analyzer analyzer = (Analyzer) Analyzer.getModelFactory("analyzer");

		switch (extension) {
		case "java":
			break;
		case "jar":
			model = analyzer.jarAnalysis(jarLocation);
			break;
		default:
			break;
		}
		
		output(outputFileName, model);
	}
	
	public void jarDenpendencies(String jarLocation, String exportDir, String exportFormat)
			throws IOException, WriterException {
		checkInput(jarLocation);
		String outputFileName = exportDir + "." + exportFormat;
		Analyzer analyzer = (Analyzer) Analyzer.getModelFactory("analyzer");
		model = analyzer.dependencyAnalysis(jarLocation);
		output(outputFileName, model);
	}
}
