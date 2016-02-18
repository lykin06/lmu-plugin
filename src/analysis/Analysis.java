package analysis;

import java.io.IOException;

import org.lucci.lmu.Model;
import org.lucci.lmu.deployementUnit.DeploymentUnit;
import org.lucci.lmu.input.ParseError;

public interface Analysis {
	
	public Model classAnalysis(String path) throws ParseError;
	public Model packageAnalysis(String path);
	public Model projectAnalysis(String path);
	public Model jarAnalysis(String path) throws ParseError;
	
	public Model dependencyAnalysis(String fileName) throws IOException;
	
	public Model createModel() throws ParseError;
	
	

}
