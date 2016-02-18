package analysis;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

import org.lucci.lmu.Entity;
import org.lucci.lmu.Model;
import org.lucci.lmu.deployementUnit.DeploymentUnit;
import org.lucci.lmu.input.ModelBuilder;
import org.lucci.lmu.input.ModelFactory;
import org.lucci.lmu.input.ParseError;
import org.lucci.lmu.test.DynamicCompiler;

import toools.ClassContainer;
import toools.ClassName;
import toools.ClassPath;
import toools.Clazz;
import toools.io.file.RegularFile;

public class Analyzer extends ModelFactory implements Analysis {

	private Collection<RegularFile> knownJarFiles = new HashSet<RegularFile>();
	private ModelBuilder modelBuilder;
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
	public Model classAnalysis(String path) throws ParseError {
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

	private DeploymentUnit buildDependencies(String fileName) throws IOException{
		JarFile input = new JarFile(fileName);
		Manifest manifest = input.getManifest();

		// Check if there is a manifest
		if (manifest != null) {
			System.out.println(manifest.toString());
			final Attributes mattr = manifest.getMainAttributes();
			for (Object key : mattr.keySet()) {
				if (key != null && (key.toString()).contains("Import-Package")) {
					System.out.println(mattr.getValue((Name) key));
				}
			}
		} else {
			System.out.println("No Dependencies");
		}
		return null;
	}
	
	@Override
	public Model dependencyAnalysis(String fileName) throws IOException{
		
		DeploymentUnit dependencies = buildDependencies(fileName);
		//TODO : put all the dependencies in a DeployementUnit Object for build
		
		
		Model model = modelBuilder.buildDependencies(new DeploymentUnit("root"));
		return model;
	}

}
