package analysis;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.lucci.lmu.Entity;
import org.lucci.lmu.Model;
import org.lucci.lmu.input.ModelBuilder;
import org.lucci.lmu.input.ModelFactory;
import org.lucci.lmu.input.ParseError;
import org.lucci.lmu.test.DynamicCompiler;

import toools.ClassContainer;
import toools.ClassName;
import toools.ClassPath;
import toools.Clazz;
import toools.io.FileUtilities;
import toools.io.file.RegularFile;

public class Analyzer extends ModelFactory implements Analysis {

	private Collection<RegularFile> knownJarFiles = new HashSet<RegularFile>();
	private Model model;
	private ModelBuilder modelBuilder;
	private List<Class<?>> classes;
	ClassPath classContainer;
	ClassLoader classLoader;
	RegularFile tempFile;
	
	public Analyzer(){
		this.modelBuilder = new ModelBuilder();
		this.classContainer = new ClassPath();
		this.tempFile = RegularFile.createTempFile("lmu-", ".jar");
		this.classLoader = new URLClassLoader(new URL[] { tempFile.toURL() });
	}
	
	@Override
	public Model classAnalysis(String path) {
		RegularFile jarFile = RegularFile.createTempFile("lmu-", ".jar");
		//jarFile.setContent(data);
		return null;
	}

	@Override
	public Model packageAnalysis(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model projectAnalysis(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model jarAnalysis(String path) throws ParseError {
		try {
			byte[] data = Files.readAllBytes(Paths.get(path));
			tempFile.setContent(data);
			classContainer.add(new ClassContainer(tempFile, classLoader));
		} catch (IOException e) {
			throw new IllegalStateException();
		}
		return createModel();
	}

	public Collection<RegularFile> getJarFiles()
	{
		return this.knownJarFiles;
	}

	@Override
	public Model createModel() throws ParseError
	{
		Model model = modelBuilder.build(classContainer.listAllClasses());
		tempFile.delete();
		return model;

	}

	protected static Class<?> createClassNamed(String fullName)
	{
		ClassName cn = Clazz.getClassName(fullName);
		String src = "";

		if (cn.pkg != null)
		{
			src += "package " + cn.pkg + ";";
		}

		src += "public class " + cn.name + " {}";

		// System.out.println(src);
		return DynamicCompiler.compile(fullName, src);
	}

	/*
	 * public static void main(String[] args) {
	 * System.out.println(createClassNamed("lucci.Coucou"));
	 * System.out.println(createClassNamed("Coucou")); }
	 */

	public String computeEntityName(Class<?> c)
	{
		return c.getName().substring(c.getName().lastIndexOf('.') + 1);
	}

	public String computeEntityNamespace(Class<?> c)
	{
		return c.getPackage() == null ? Entity.DEFAULT_NAMESPACE : c.getPackage().getName();
	}

}
