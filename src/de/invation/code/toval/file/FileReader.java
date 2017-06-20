package de.invation.code.toval.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class FileReader {
	
	protected Charset charset = Charset.forName("UTF-8");
	protected BufferedReader input = null;
	protected File inputFile = null;
	protected String systemLineSeparatorBackup = null;
	
	//------- Constructors --------------------------------------------------------------------
	
	public FileReader(String fileName) throws IOException{
		initialize(fileName);
	}
	
	public FileReader(String fileName, Charset charset) throws IOException{
		setCharset(charset);
		initialize(fileName);
	}
	
	//------- Getters and Setters -------------------------------------------------------------
	
	public Charset getCharset(){
		return charset;
	}
	
	private void setCharset(Charset charset){
		this.charset = charset;
	}
	
	public File getFile(){
		return inputFile;
	}
	
	
	//------- Methods for setting up the file reader -----------------------------------------
	
	private void initialize(String fileName) throws IOException{
		prepareFile(fileName);
		prepareReader();
	}
	
	protected void prepareFile(String fileName) throws IOException{
		inputFile = new File(fileName);
		if(inputFile.isDirectory())
			throw new IOException("I/O Error on opening file: File is a directory!");
		if(!inputFile.exists())
			throw new IOException("I/O Error on opening file: File does not exist!");
		if(!inputFile.canRead())
			throw new IOException("I/O Error on opening file: Unable to read file!");
	}
	
	protected void prepareReader() throws IOException{
		input = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), charset));
		adjustSystemProperties();
	}
	
	protected void adjustSystemProperties() throws IOException{
		systemLineSeparatorBackup = System.getProperty("line.separator");
		@SuppressWarnings("resource")
		InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile), charset);
		String lineSeparator = null;
		int next;
		while(lineSeparator == null && (next = reader.read()) != -1){
			char nextChar = (char) next;
			if(nextChar == '\n'){
				lineSeparator = "\n";
				break;
			} else if(nextChar == '\r'){
				lineSeparator = "\r";
				next = reader.read();
				if(next != -1 && ((char) next) == '\n'){
					lineSeparator += "\n";
				}
				break;
			}
		}
		if(lineSeparator != null){
			System.setProperty("line.separator", lineSeparator);
		}
	}
	
	protected void restoreSystemProperties(){
		System.setProperty("line.separator", systemLineSeparatorBackup);
	}
	
	public BufferedReader getReader() {
		return input;
	}
	
	//------- Functionality ------------------------------------------------------------------
	

	public String readLine() throws IOException{
		return input.readLine();
	}
	
	public void closeFile() throws IOException{
		input.close();
		restoreSystemProperties();
	}
	
	
//	public static void main(String[] args) throws Exception{
//		FileReader reader = new FileReader("NewFile.txt", Charset.forName("MacRoman"));
//		String nextLine;
//		while((nextLine = reader.readLine()) != null){
//			System.out.println(nextLine);
//		}
//	}

}
