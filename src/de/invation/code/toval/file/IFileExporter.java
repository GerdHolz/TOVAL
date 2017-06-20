package de.invation.code.toval.file;

import java.io.File;

public interface IFileExporter {
	
	public File getOutputFile();
	
	public String getFileName();
	
	public void createFile() throws Exception;
	
	public boolean contentsToExport();

}
