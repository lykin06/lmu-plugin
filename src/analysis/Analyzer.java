package analysis;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
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

	@Override
	public void classAnalysis(String path, List<Class<?>> classes) {
		RegularFile jarFile = RegularFile.createTempFile("lmu-", ".jar");
		jarFile.setContent(data);
		
	}

	@Override
	public void packageAnalysis(String path, List<Class<?>> classes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void projectAnalysis(String path, List<Class<?>> classes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jarAnalysis(String path, List<Class<?>> classes) {
		// TODO Auto-generated method stub
		
	}

	private Collection<RegularFile> knownJarFiles = new HashSet<RegularFile>();

	public Collection<RegularFile> getJarFiles()
	{
		return this.knownJarFiles;
	}

	@Override
	public Model createModel(byte[] data) throws ParseError
	{
		

		try
		{

			// create a jar file on the disk from the binary data
			RegularFile jarFile = RegularFile.createTempFile("lmu-", ".jar");
			jarFile.setContent(data);
			ClassLoader classLoader = new URLClassLoader(new URL[] { jarFile.toURL() });
			ClassPath classContainers = new ClassPath();
			classContainers.add(new ClassContainer(jarFile, classLoader));

			for (RegularFile thisJarFile : this.knownJarFiles)
			{
				classContainers.add(new ClassContainer(thisJarFile, classLoader));
			}


			// at this only the name of entities is known
			// neither members nor relation are known
			// let's find them
			
			ModelBuilder modelBuilder = new ModelBuilder();
			
			Model model = modelBuilder.build(classContainers.listAllClasses());
			
			jarFile.delete();
			
			return model;
		}
		catch (IOException ex)
		{
			throw new IllegalStateException();
		}

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

	

	public Model createModel(File file) throws ParseError, IOException
	{
		byte[] data = FileUtilities.getFileContent(file);
		return createModel(data);
	}

}
