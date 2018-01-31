/**
 * 
 */
package ceka.consensus.square;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import ceka.core.Dataset;
import ceka.core.Example;
import ceka.core.Label;

/**
 * SquareResultsAnalyzer is used to analyze the results of Square
 * @author Zhang
 *
 */
public class SquareResultsAnalyzer {
	
	public SquareResultsAnalyzer(String squareDir) {
		this.resultsDir = new String (squareDir + aggregatedDir);
		this.categoryMapPath = new String (squareDir + modelDir + categoryMapFile);
	}
	
	/**
	 * analyze the result generated by square
	 * @param methodName
	 * @param estimationType
	 * @throws IOException
	 */
	public void analyze(String methodName, String estimationType) throws IOException {
		this.methodName = new String(methodName);
		String resultPath = resultsDir + this.methodName + "_" + estimationType +"_aggregated.txt";
		
		// read category model file
		HashMap<Integer, String> cateMap = new HashMap<Integer, String>(); // (square-processed, original)
		FileReader readerCatePair= new FileReader(categoryMapPath);
		BufferedReader buffCatePair = new BufferedReader(readerCatePair);
		String strCate = null;
		while((strCate = buffCatePair.readLine()) != null) {
			String [] subStrsCate = strCate.split("[ ]");
			Integer cateInt = Integer.parseInt(subStrsCate [1]);
			cateMap.put(cateInt, subStrsCate[0]);
		}
		readerCatePair.close();
		buffCatePair.close();
		
		// read result file
		FileReader readerRst = new FileReader(resultPath);
		BufferedReader buffRst = new BufferedReader(readerRst);
		String strRst = null;
		while((strRst = buffRst.readLine()) != null) {
			String [] substrsRst = strRst.split("[ ]");
			String exampleId = substrsRst[0];
			Integer cateInt = Integer.parseInt(substrsRst[1]);
			String originalCate = cateMap.get(cateInt);
			resultMap.put(exampleId, originalCate);
		}
		readerRst.close();
		buffRst.close();
	}
	
	/**
	 * assign integrated label to each example in dataset
	 * @param dataset
	 */
	public void assignIntegratedLabel(Dataset dataset) {
		for (int i = 0; i < dataset.getExampleSize(); i++) {
			Example example = dataset.getExampleByIndex(i);
			String cate = resultMap.get(example.getId());
			Label integratedL = new Label(null, cate, example.getId(), methodName);
			example.setIntegratedLabel(integratedL);
		}
	}
	
	private String resultsDir = null;
	private String categoryMapPath = null;
	private String methodName = null;
	private HashMap<String, String> resultMap = new HashMap<String, String>();
	
	private static String modelDir = "model/";
	private static String aggregatedDir = "results/nFold/aggregated/";
	private static String categoryMapFile = "map_category_integer.txt";
}
