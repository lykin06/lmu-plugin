package org.lucci.lmu.test;

import java.io.File;
import java.io.IOException;

import org.lucci.lmu.Entities;
import org.lucci.lmu.Entity;
import org.lucci.lmu.Model;
import org.lucci.lmu.input.JarFileAnalyser;
import org.lucci.lmu.input.ParseError;

public class Test
{
	public static void main(String... args) throws ParseError, IOException
	{
		 Model model = new JarFileAnalyser().createModel(new File("/home/mathieu/Documents/Polytech_SI5/lmu/src/org/lucci/lmu/AssociationRelation.java"));
		 Entity e = Entities.findEntityByName(model, "WaitExpression");
		 System.out.println(e.getName());
		 System.out.println(e.getAttributes());
	}
}
