package analysis;

import java.util.List;

import org.lucci.lmu.Model;
import org.lucci.lmu.input.ParseError;

public interface Analysis {
	
	public Model classAnalysis(String path);
	public Model packageAnalysis(String path);
	public Model projectAnalysis(String path);
	public Model jarAnalysis(String path) throws ParseError;
	public Model createModel() throws ParseError;

}
