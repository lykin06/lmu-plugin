package org.lucci.lmu.input;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lucci.lmu.AssociationRelation;
import org.lucci.lmu.Attribute;
import org.lucci.lmu.Entities;
import org.lucci.lmu.Entity;
import org.lucci.lmu.InheritanceRelation;
import org.lucci.lmu.Model;
import org.lucci.lmu.Operation;
import org.lucci.lmu.Visibility;
import org.lucci.lmu.test.DynamicCompiler;

import toools.ClassContainer;
import toools.ClassName;
import toools.ClassPath;
import toools.Clazz;
import toools.io.FileUtilities;
import toools.io.file.RegularFile;

/*
 * Created on Oct 11, 2004
 */

/**
 * @author luc.hogie
 */
public class JarFileAnalyser extends ModelFactory
{
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
