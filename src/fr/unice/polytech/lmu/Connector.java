package fr.unice.polytech.lmu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.lucci.lmu.FileChooser;
import org.lucci.lmu.Model;
import org.lucci.lmu.input.JarFileAnalyser;
import org.lucci.lmu.input.ParseError;
import org.lucci.lmu.output.AbstractWriter;
import org.lucci.lmu.output.WriterException;

public class Connector {
	Model model;
	
	public Connector() {
		model = new Model();
	}
	
	public void exportJar(String jarLocation, String exportDir, String exportFormat)
			throws ParseError, IOException, WriterException {
		File output = new File(exportDir + exportFormat);
		JarFileAnalyser jf = (JarFileAnalyser) JarFileAnalyser.getModelFactory("jar");
		model = jf.createModel(Files.readAllBytes(Paths.get(jarLocation)));
		AbstractWriter factory = AbstractWriter.getTextFactory(FileChooser.getFileExtension(output.getName()));
		FileOutputStream fis = new FileOutputStream(output);
		fis.write(factory.writeModel(model));
		fis.flush();
		fis.close();
	}
}
