package de.invation.code.toval.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collection;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.ParameterException.ErrorCode;
import de.invation.code.toval.validate.Validate;


public class FileWriter {
	
	public static final String DEFAULT_PATH = "";
	public static final String DEFAULT_FILE_NAME = "NewFile";
	public static final String DEFAULT_FILE_EXTENSION = "txt";
	public static final String DEFAULT_EOL_STRING = System.getProperty("line.separator");
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
	protected String path = getDefaultPath();
	protected String fileName = getDefaultFileName();
	protected Charset charset = DEFAULT_CHARSET;
	protected String eolString = DEFAULT_EOL_STRING;
	protected BufferedWriter output = null;
	protected File outputFile = null;
	protected String fileExtension = DEFAULT_FILE_EXTENSION;
	
	//------- Constructors -------------------------------------------------------------------
	
	/**
	 * Creates a new file writer.<br>
	 * The constructor uses default values for:<br>
	 * file name<br>
	 * path<br>
	 * charset<br>
	 * eol-string<br>
	 * @see #DEFAULT_FILE_NAME
	 * @see #DEFAULT_PATH
	 * @see #DEFAULT_CHARSET
	 * @see #DEFAULT_EOL_STRING
	 */
	public FileWriter() {
		try{
			initialize(getDefaultFileName(), getDefaultPath());
		}catch(ParameterException e){
			// Is only thrown if default path or file names are invalid.
			// Should be avoided by setting the static fields appropriately.
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a new file writer.<br>
	 * The constructor uses default values for:<br>
	 * path<br>
	 * charset<br>
	 * eol-string<br>
	 * @see #DEFAULT_PATH
	 * @see #DEFAULT_CHARSET
	 * @see #DEFAULT_EOL_STRING
	 * @param fileName The name for the output file.
	 * @throws ParameterException if the file name is <code>null</code> or an empty string.
	 */
	public FileWriter(String fileName) throws ParameterException {
		initialize(fileName, getDefaultPath());
	}
	
	/**
	 * Creates a new file writer.<br>
	 * The constructor uses default values for:<br>
	 * charset<br>
	 * eol-string<br>
	 * @see #DEFAULT_CHARSET
	 * @see #DEFAULT_EOL_STRING
	 * @param path The path for the output file.
	 * @param fileName The name for the output file.
	 * @throws ParameterException if some parameters are <code>null</code>, <br>
	 * the file path is not a directory or the file name is an empty string.
	 */
	public FileWriter(String path, String fileName) throws ParameterException{
		initialize(fileName, path);
	}
	
	/**
	 * Creates an new file writer.<br>
	 * The constructor uses default values for:<br>
	 * file name<br>
	 * path<br>
	 * eol-string<br>
	 * @see #DEFAULT_FILE_NAME
	 * @see #DEFAULT_PATH
	 * @see #DEFAULT_EOL_STRING
	 * @param charset Charset to use for output file.
	 * @throws ParameterException if the given charset is <code>null</code>.
	 */
	public FileWriter(Charset charset) throws ParameterException{
		setCharset(charset);
		initialize(getDefaultFileName(), getDefaultPath());
	}
	
	/**
	 * Creates a new file writer.<br>
	 * The constructor uses default values for:<br>
	 * path<br>
	 * eol-string<br>
	 * @see #DEFAULT_FILE_NAME
	 * @see #DEFAULT_EOL_STRING
	 * @param fileName The name for the output file.
	 * @param charset Charset to use for output file.
	 * @throws ParameterException if some parameters are <code>null</code> or the file name is an empty string.
	 */
	public FileWriter(String fileName, Charset charset) throws ParameterException {
		setCharset(charset);
		initialize(fileName, getDefaultPath());
	}
	
	/**
	 * Creates a new file writer.<br>
	 * The constructor uses default values for:<br>
	 * eol-string<br>
	 * @see #DEFAULT_EOL_STRING
	 * @param path The path for the output file.
	 * @param fileName The name for the output file.
	 * @param charset Charset to use for output file.
	 * @throws ParameterException if some parameters are <code>null</code> or file name is an empty string.
	 */
	public FileWriter(String path, String fileName, Charset charset) throws ParameterException{
		setCharset(charset);
		initialize(fileName, path);
	}
	
	/**
	 * Creates a new file writer.<br>
	 * The constructor uses default values for:<br>
	 * file name<br>
	 * path<br>
	 * charset<br>
	 * @see #DEFAULT_FILE_NAME
	 * @see #DEFAULT_PATH
	 * @see #DEFAULT_CHARSET
	 * @param eolType
	 * @see EOLType The eol property.
	 * @throws ParameterException if the eolType is <code>null</code>.
	 */
	public FileWriter(EOLType eolType) throws ParameterException{
		this();
		setEOLString(eolType);
	}
	
	/**
	 * Creates a new file writer.<br>
	 * The constructor uses default values for:<br>
	 * path<br>
	 * charset<br>
	 * @see #DEFAULT_PATH
	 * @see #DEFAULT_CHARSET
	 * @param fileName The name for the output file.
	 * @param eolType The eol property.
	 * @throws ParameterException if some parameters are <code>null</code> or the file name is an empty string.
	 */
	public FileWriter(String fileName, EOLType eolType) throws ParameterException {
		this(fileName);
		setEOLString(eolType);
	}
	
	/**
	 * Creates a new file writer.<br>
	 * The constructor uses default values for:<br>
	 * charset<br>
	 * @see #DEFAULT_CHARSET
	 * @param path The path for the output file.
	 * @param fileName The name for the output file.
	 * @param eolType The eol property.
	 * @throws ParameterException if some parameters are <code>null</code> or the file name is an empty string.
	 */
	public FileWriter(String path, String fileName, EOLType eolType) throws ParameterException{
		this(path, fileName);
		setEOLString(eolType);
	}
	
	/**
	 * Creates a new file writer.<br>
	 * The constructor uses default values for:<br>
	 * file name<br>
	 * path<br>
	 * @see #DEFAULT_FILE_NAME
	 * @see #DEFAULT_PATH
	 * @param charset Charset to use for output file.
	 * @param eolType The eol property.
	 * @throws ParameterException if some parameters are <code>null</code>
	 */
	public FileWriter(Charset charset, EOLType eolType) throws ParameterException{
		this(charset);
		setEOLString(eolType);
	}
	
	/**
	 * Creates a new file writer.<br>
	 * The constructor uses default values for:<br>
	 * path<br>
	 * @see #DEFAULT_PATH
	 * @param fileName The name for the output file.
	 * @param charset Charset to use for output file.
	 * @param eolType The eol property.
	 * @throws ParameterException if some parameters are <code>null</code> or the file name is an empty string.
	 */
	public FileWriter(String fileName, Charset charset, EOLType eolType) throws ParameterException {
		this(fileName, charset);
		setEOLString(eolType);
	}
	
	/**
	 * Creates a new file writer.<br>
	 * @param path The path for the output file.
	 * @param fileName The name for the output file.
	 * @param charset Charset to use for output file.
	 * @param eolType The eol property.
	 * @throws ParameterException if some parameters are <code>null</code> or the file name is an empty string.
	 */
	public FileWriter(String path, String fileName, Charset charset, EOLType eolType) throws ParameterException{
		this(path, fileName, charset);
		setEOLString(eolType);
	}
	
	
	//------- Getters and Setters ------------------------------------------------------------
	
	public String getFileName() {
		return String.format("%s%s.%s", getPath(), fileName, getFileExtension());
	}
	
	public String getDefaultFileName(){
		return DEFAULT_FILE_NAME;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getDefaultPath(){
		return DEFAULT_PATH;
	}
	
	public String getFileExtension(){
		return fileExtension;
	}
	
	public void setFileExtension(String extension){
		this.fileExtension = extension;
	}
	
	public String getCharset(){
		return charset.name();
	}
	
	private void setCharset(Charset charset) throws ParameterException{
		Validate.notNull(charset);
		this.charset = charset;
	}
	
	public String getEOLString(){
		return eolString;
	}
	
	protected void setEOLString(EOLType eolType) throws ParameterException{
		Validate.notNull(eolType);
		this.eolString = eolType.toString();
	}
	
	public File getFile(){
		return outputFile;
	}
	
	//------- Methods for setting up the file writer -----------------------------------------
	
	/**
	 * Initializes the file writer with the given file and path names.
	 * @param fileName
	 * @param path
	 * @throws ParameterException if some parameters are <code>null</code>, <br>
	 * the file path is not a directory or the file name is an empty string.
	 */
	private synchronized void initialize(String fileName, String path) throws ParameterException {
		if(!path.equals(this.path)){
			checkPath(path);
			this.path = path;
		}
		if(!fileName.equals(this.fileName)){
			checkFileName(fileName);
			this.fileName = fileName;
		}
	}

	/**
	 * Sets the path for the file writer where output files are put in.
	 * @param logPath Desired log path.
	 * @throws ParameterException if the given path is <code>null</null> or not a directory.
	 */
	private synchronized void checkPath(String logPath) throws ParameterException {
		Validate.notNull(path);
		File cPath = new File(logPath);
		if(!cPath.exists())
			cPath.mkdirs();
		if(!cPath.isDirectory())
			throw new ParameterException(ErrorCode.INCOMPATIBILITY, logPath + " is not a valid path!");
	}
	
	/**
	 * Sets the name for the output file.
	 * @param fileName Desired file name.
	 * @throws IllegalArgumentException if the given file name is empty.
	 */
	private synchronized void checkFileName(String fileName) throws ParameterException{
		Validate.notNull(fileName);
		File cFile = new File(fileName);
		if(cFile.getName().length()==0)
			throw new ParameterException(ErrorCode.INCOMPATIBILITY, fileName + " is not a valid file-name!");
	}
	
	/**
	 * Creates the output file on the file system.
	 * @throws IOException if the output file is a directory or not writable.
	 */
	private synchronized void prepareFile() throws IOException{
		outputFile = new File(getFileName());
		if(outputFile.exists()) 
			outputFile.delete();
		if(outputFile.isDirectory())
			throw new IOException("I/O Error on creating file: File is a directory!");
		outputFile.createNewFile();
		if(!outputFile.canWrite())
			throw new IOException("I/O Error on creating file: Unable to write into file!");
	}
	
	/**
	 * Creates a buffered writer for writing content into the output file.
	 * @throws IOException if the file cannot be found.
	 */
	private synchronized void prepareWriter() throws IOException{
		output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), charset));
	}
	
	/**
	 * Creates the output file and a buffered writer for filling it with content.
	 * @throws IOException if file or writer could not be created.
	 * @see #prepareFile()
	 * @see #prepareWriter()
	 */
	protected synchronized void prepare() throws IOException{
		if(output == null){
			prepareFile();
			prepareWriter();
		}
	}
	
	
	//------- Functionality ------------------------------------------------------------------
	
	public synchronized void write(Object obj) throws IOException{
		prepare();
		output.write(obj.toString());
	}
	
	public synchronized void writeLines(Collection<?> obj) throws IOException{
		for(Object object: obj)
			writeLine(object);
	}
	
	public synchronized void writeLine(Object obj) throws IOException{
		prepare();
		output.write(obj.toString());
		output.write(eolString);
		output.flush();
	}
	
	public synchronized void newLine() throws IOException{
		prepare();
		output.newLine();
		output.flush();
	}
	
	public synchronized void closeFile() throws IOException {
		if(output != null){
			output.close();
		}
	}

}
