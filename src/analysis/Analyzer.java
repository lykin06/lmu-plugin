package analysis;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
	private String pluginDir;

	public Analyzer() {
		this.modelBuilder = new ModelBuilder();
		this.classContainer = new ClassPath();
		this.tempFile = RegularFile.createTempFile("lmu-", ".jar");
		this.classLoader = new URLClassLoader(new URL[] { tempFile.toURL() });
	}

	public String getPluginDir() {
		return pluginDir;
	}

	public void setPluginDir(String pluginDir) {
		this.pluginDir = pluginDir;
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
		Model model = modelBuilder.buildUML(classContainer.listAllClasses());
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

		// String[] test = Arrays.copyOf(tokens, tokens.length - 1);
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

	private String findFile(String dependency) {
		File folder = new File(pluginDir);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String name = listOfFiles[i].getName();
				if (name.startsWith(dependency + "_")) {
					System.out.println("File " + listOfFiles[i].getName());
					return listOfFiles[i].getName();
				}
			}
			// } else if (listOfFiles[i].isDirectory()) {
			// System.out.println("Directory " + listOfFiles[i].getName());
			// }
		}
		return null;
	}

	private DeploymentUnit buildDependencies(String fileName, List<String> depList, int level) {
		DeploymentUnit du = new DeploymentUnit(fileName);
		System.out.println(fileName);

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
								// System.out.println(d);
								depList.add(d);
								String depFile = findFile(d);
								if ((depFile != null) && (level > 0)) {
									DeploymentUnit duDep = buildDependencies(pluginDir + "/" + depFile, depList,
											level - 1);
									duDep.setName(d);
									du.getDependencies().add(duDep);
								} else {
									du.getDependencies().add(new DeploymentUnit(d));
								}
							}
						}
					}
				}
			} else {
				System.out.println("No Dependencies");
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		return du;
	}

	@Override
	public Model dependencyAnalysis(String fileName, int level) throws IOException {
		List<String> depList = new ArrayList<>();
		depList.add(fileName);
		DeploymentUnit dependencies = buildDependencies(fileName, depList, level);
		
		System.out.println("done dependencies\n\n");
		System.out.println(dependencies);
		Model model = modelBuilder.buildDependencies(dependencies);
		return model;
	}

}
