package analysis;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

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

	public Analyzer() {
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

	public Collection<RegularFile> getJarFiles() {
		return this.knownJarFiles;
	}

	@Override
	public Model createModel() throws ParseError {
		Model model = modelBuilder.build(classContainer.listAllClasses());
		tempFile.delete();
		return model;

	}

	protected static Class<?> createClassNamed(String fullName) {
		ClassName cn = Clazz.getClassName(fullName);
		String src = "";

		if (cn.pkg != null) {
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

	public String computeEntityName(Class<?> c) {
		return c.getName().substring(c.getName().lastIndexOf('.') + 1);
	}

	public String computeEntityNamespace(Class<?> c) {
		return c.getPackage() == null ? Entity.DEFAULT_NAMESPACE : c.getPackage().getName();
	}

	private List<String> cleanDependencies(String dependencies) {
		String[] tokens = dependencies.split(",");
		List<String> dep = new ArrayList<String>();
		
//		String[] test = Arrays.copyOf(tokens, tokens.length - 1);
		for (String s : tokens) {
			if (s.endsWith(".jar")) {
				dep.add(s);
				continue;
			}
			if (s.contains(";")) {
				String[] temp = s.split(";");
				dep.add(temp[0]);
				continue;
			}
		}

		return dep;
	}

	private boolean containsDep(List<String> depList, String name) {
		for (String d : depList) {
			if (d != null && d.equals(name)) {
				return true;
			}
		}
		return false;
	}

	private DeploymentUnit buildDependencies(String fileName, List<String> depList) {
		DeploymentUnit du = new DeploymentUnit(fileName);
		depList.add(fileName);

		try {
			JarFile input = new JarFile(fileName);
			Manifest manifest = input.getManifest();

			// Check if there is a manifest
			if (manifest != null) {
				// Find the dependencies
				final Attributes mattr = manifest.getMainAttributes();
				for (Object key : mattr.keySet()) {
					if (key != null && ((key.toString()).contains("Import-Package")
							|| (key.toString()).contains("Bundle-ClassPath") || (key.toString()).contains("Class-Path")
							|| (key.toString()).contains("Require-Bundle"))) {
						List<String> dependencies = cleanDependencies(mattr.getValue((Name) key));
						for (String d : dependencies) {
							if (!containsDep(depList, d)) {
								//System.out.println(d);
								depList.add(d);
								du.getDependencies().add(buildDependencies(d, depList));
								// File f = new File(System.getProperty(d));
								// File dir =
								// f.getAbsoluteFile().getParentFile();
								// System.out.println(dir.toString());
							}
						}
					}
				}
			} else {
				System.out.println("No Dependencies");
			}
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println(e.getMessage());
		}

		return du;
	}

	@Override
	public Model dependencyAnalysis(String fileName) throws IOException {
		List<String> depList = new ArrayList<>();
		DeploymentUnit dependencies = buildDependencies(fileName, depList);
		Model model = modelBuilder.buildDependencies(dependencies);
		return model;
	}

}
