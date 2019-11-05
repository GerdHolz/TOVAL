package de.invation.code.toval.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;


public class LineBasedFileTransformer {
	
	protected Charset inputCharset = Charset.forName("UTF-8");
	protected Charset outputCharset = Charset.forName("UTF-8");

	protected boolean omitFirstLine = false;

	protected FileReader fileReader = null;
	protected FileWriter fileWriter = null;
	
	private String fileExtension = null;
	
	private int inputLines = 0;
	private int outputLines = 0;
	
	private final boolean DEFAULT_USE_EXTENSION_OF_INPUT_FILE = true;
	private boolean useExtensionOfInputFile = DEFAULT_USE_EXTENSION_OF_INPUT_FILE;
	
	private static final String DEFAULT_FILE_NAME_SUFFIX = "_output";
	private String fileNameSuffix = DEFAULT_FILE_NAME_SUFFIX;
	
	
	//------- Constructors -------------------------------------------------------------------
	
	public LineBasedFileTransformer(){}
	
	public LineBasedFileTransformer(Charset charset) throws ParameterException{
		this(charset, charset);
	}
	
	public LineBasedFileTransformer(Charset inputCharset, Charset outputCharset) throws ParameterException{
		Validate.notNull(inputCharset);
		Validate.notNull(outputCharset);
		this.inputCharset = inputCharset;
		this.outputCharset = outputCharset;
	}
	
	
	//------- Getters and Setters ------------------------------------------------------------
	
	public String getInputCharset(){
		return inputCharset.toString();
	}
	
	public String getOutputCharset(){
		return inputCharset.toString();
	}
	
	public boolean omitsFirstLine(){
		return omitFirstLine;
	}
	
	public void setOmitFirstLine(boolean omitFirstLine){
		this.omitFirstLine = omitFirstLine;
	}
	
	public String getFileExtension(){
		return fileExtension;
	}
	
	public void setFileExtension(String extension){
		this.fileExtension = extension;
	}
	
	protected String getHeaderLine() {
		return null;
	}
	
	public int getInputLines() {
		return inputLines;
	}

	public int getOutputLines() {
		return outputLines;
	}
	
	public String getFileNameSuffix() {
		return fileNameSuffix;
	}

	public void setFileNameSuffix(String fileNameSuffix) {
		this.fileNameSuffix = fileNameSuffix;
	}
	
	public boolean isUseExtensionOfInputFile() {
		return useExtensionOfInputFile;
	}

	public void setUseExtensionOfInputFile(boolean useExtensionOfInputFile) {
		this.useExtensionOfInputFile = useExtensionOfInputFile;
	}
	
	//------- Methods for setting up the parser ----------------------------------------------
	
	protected synchronized void initialize(File file, Charset inputCharset, Charset outputCharset) throws IOException, ParameterException {
		fileReader = new FileReader(file, inputCharset);
		String inputName = file.getAbsolutePath();
		String outputFileName = inputName.substring(0, inputName.indexOf('.')).concat(fileNameSuffix);
		fileWriter = new FileWriter(outputFileName, outputCharset);
		if(fileExtension != null){
			fileWriter.setFileExtension(getFileExtension());
		} else if(useExtensionOfInputFile){
			fileWriter.setFileExtension(FileUtils.getExtension(fileReader.getFile()));
		}
	}
	
	public File parseFile(String fileName) throws IOException, ParameterException{
		return parseFile(fileName, inputCharset, outputCharset);
	}
		
	public File parseFile(String file, Charset charset) throws IOException, ParameterException{
		return parseFile(file, charset, charset);
	}
	
	public File parseFile(String file, Charset inputCharset, Charset outputCharset) throws IOException, ParameterException{
		return parseFile(new File(file), inputCharset, outputCharset);
	}
	
	public File parseFile(File file) throws IOException, ParameterException{
		return parseFile(file, inputCharset, outputCharset);
	}
	
	public File parseFile(File file, Charset charset) throws IOException, ParameterException{
		return parseFile(file, charset, charset);
	}
	
	public File parseFile(File file, Charset inputCharset, Charset outputCharset) throws IOException, ParameterException{
		initialize(file, inputCharset, outputCharset);
		
		String headerLine = getHeaderLine();
		if(headerLine != null)
			fileWriter.writeLine(headerLine);
		
		String line = null;
		int lineCount = 0;
		while ((line = fileReader.readLine()) != null) {
			if(lineCount == 0 && omitFirstLine){
				lineCount++;
				continue;
			} else {
				transformAndWriteOutputLine(line);
				lineCount++;
			}
			if(!continueParsing(lineCount))
				break;
		}
		inputLines = lineCount;
		fileReader.closeFile();
		endOfFileReached();
		fileWriter.closeFile();
		return fileWriter.getFile();
	}

	protected  void endOfFileReached() throws IOException{}

	protected boolean continueParsing(int lineCount){
		return true;
	}
	
	private synchronized void transformAndWriteOutputLine(String outputLine) throws IOException{
		List<String> lines = transformLine(outputLine);
		if(lines == null || lines.isEmpty())
			return;
		for(String line: lines){
			writeOutputLine(line);
		}
		System.out.println();
	}
	
	protected synchronized void writeOutputLine(String outputLine) throws IOException{
		fileWriter.writeLine(outputLine);
		outputLines++;
	}
	
	protected synchronized List<String> transformLine(String line){
		return new ArrayList<String>(Arrays.asList(line));
	}

}
