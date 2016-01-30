package analysis;

import java.util.List;

import org.lucci.lmu.Model;
import org.lucci.lmu.input.ParseError;

public interface Analysis {
	
	public void classAnalysis(String path, List<Class<?>> classes);
	public void packageAnalysis(String path, List<Class<?>> classes);
	public void projectAnalysis(String path, List<Class<?>> classes);
	public void jarAnalysis(String path, List<Class<?>> classes);
	public Model createModel(byte[] data) throws ParseError;

}
