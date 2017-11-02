package de.invation.code.toval.file;

import java.io.File;

/**
 * Interface for file exporters.<br>
 * <br>
 * Used e.g. for excel and word exporters (poiutils).
 *
 */
public interface IFileExporter {
	
	/**
	 * Returns the output file (i.e. file which is used to write contents).<br>
	 * @return
	 */
	public File getOutputFile();
	
	/**
	 * Returns the file name part of the output file.
	 * @return
	 */
	default String getFileName(){
		return FileUtils.getFile(getOutputFile());
	}
	
	/**
	 * Creates the output file.
	 * @throws Exception
	 */
	public void createFile() throws Exception;
	
	/**
	 * Indicates whether there is content to export.<br>
	 * Can be used to abort file creation when there is no content.
	 * @return {@code true} when there is content to export; {@code false} otherwise.
	 */
	public boolean contentsToExport();

}
