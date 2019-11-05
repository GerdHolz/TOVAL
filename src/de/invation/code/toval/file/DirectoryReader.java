package de.invation.code.toval.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.invation.code.toval.validate.Validate;

public abstract class DirectoryReader {
	
	private static final Logger log = LogManager.getLogger(DirectoryReader.class);
	
	private static final boolean DEFAULT_CONSIDER_INVISIBLE_FILES = true;
	private static final boolean DEFAULT_CONSIDER_FILES = true;
	private static final boolean DEFAULT_CONSIDER_DIRECTORIES = true;
	
	private static final boolean DEFAULT_STOP_ON_FIRST_FILE_PROCESSING_EXCEPTION = false;
	private static final boolean DEFAULT_STOP_ON_FIRST_DIRECTORY_PROCESSING_EXCEPTION = false;
	
	private boolean considerInvisibleFiles = DEFAULT_CONSIDER_INVISIBLE_FILES;
	private boolean considerFiles = DEFAULT_CONSIDER_FILES;
	private boolean considerDirectories = DEFAULT_CONSIDER_DIRECTORIES;
	private boolean stopOnFirstFileProcessingException = DEFAULT_STOP_ON_FIRST_FILE_PROCESSING_EXCEPTION;
	private boolean stopOnFirstDirectoryProcessingException = DEFAULT_STOP_ON_FIRST_DIRECTORY_PROCESSING_EXCEPTION;
	private Set<String> acceptedEndings = null;
	
	private Map<File, Exception> processingExceptions = new HashMap<>();

	public boolean isConsiderInvisibleFiles() {
		return considerInvisibleFiles;
	}

	public void setConsiderInvisibleFiles(boolean considerInvisibleFiles) {
		this.considerInvisibleFiles = considerInvisibleFiles;
	}

	public boolean isConsiderFiles() {
		return considerFiles;
	}

	public void setConsiderFiles(boolean considerFiles) {
		this.considerFiles = considerFiles;
	}

	public boolean isConsiderDirectories() {
		return considerDirectories;
	}

	public void setConsiderDirectories(boolean considerDirectories) {
		this.considerDirectories = considerDirectories;
	}

	public Set<String> getAcceptedEndings() {
		return acceptedEndings;
	}

	public void setAcceptedEndings(Set<String> acceptedEndings) {
		this.acceptedEndings = acceptedEndings;
	}

	public boolean isStopOnFirstFileProcessingException() {
		return stopOnFirstFileProcessingException;
	}

	public void setStopOnFirstFileProcessingException(boolean stopOnFirstFileProcessingException) {
		this.stopOnFirstFileProcessingException = stopOnFirstFileProcessingException;
	}

	public boolean isStopOnFirstDirectoryProcessingException() {
		return stopOnFirstDirectoryProcessingException;
	}

	public void setStopOnFirstDirectoryProcessingException(boolean stopOnFirstDirectoryProcessingException) {
		this.stopOnFirstDirectoryProcessingException = stopOnFirstDirectoryProcessingException;
	}

	public void readCurrentDirectory(boolean recursive) throws Exception{
		readDirectory(new File("."), recursive);
	}
	
	public void readDirectory(File directory, boolean recursive) throws Exception{
		log.info("Validating input directory...");
		Validate.directory(directory);
		readDirectoryInternal(directory, recursive);
	}
	
	private void readDirectoryInternal(File directory, boolean recursive) throws Exception{
		log.info("Reading directory '{}'", directory.getName());
		if(!readFilesInDirectory(directory) && !readSubdirectoriesInDirectory(directory))
			return;
		log.info("Getting directory content...");
		log.debug("Read          files: {}", readFilesInDirectory(directory));
		log.debug("Read subdirectories: {}", readSubdirectoriesInDirectory(directory));
		List<File> filesInDirectory = null;
		if(readFilesInDirectory(directory)){
			filesInDirectory = FileUtils.getFilesInDirectory(directory.getAbsolutePath(), !readSubdirectoriesInDirectory(directory), !considerInvisibleFiles, acceptedEndings, false);
		} else if(readSubdirectoriesInDirectory(directory)){
			filesInDirectory = FileUtils.getSubdirectories(directory);
		}
		
		log.info("Processing directory content...");
		List<File> regularFiles = new ArrayList<>();
		List<File> subDirectories = new ArrayList<>();
		for(File file: filesInDirectory){
			if(file.isDirectory()){
				log.debug("Found subdir: {}", file.getName());
				subDirectories.add(file);
				continue;
			}
			if(file.isFile()){
				log.debug("Found file: {}", file.getName());
				regularFiles.add(file);
				continue;
			}
		}
		log.debug("Directory content: {} files, {} sub directories", regularFiles.size(), subDirectories.size());
		for(File regularFile: regularFiles){
			if(considerFiles)
				processFileInternal(regularFile);
		}
		for(File subDirectory: subDirectories){
			if(considerDirectories)
				processDirectoryInternal(subDirectory);
			if(recursive && readDirectoryContents(subDirectory))
				readDirectoryInternal(subDirectory, recursive);
		}
	}
	
	private void processDirectoryInternal(File directory) throws Exception{
		try {
			log.info("Processing directory '{}'...", directory.getName());
			processDirectory(directory);
		} catch(Exception e){
			log.error(e);
			processingExceptions.put(directory, e);
			if(isStopOnFirstDirectoryProcessingException())
				throw e;
		}
	}
	
	private void processFileInternal(File file) throws Exception{
		try {
			log.info("Processing file '{}'...", file.getName());
			processFile(file);
		} catch(Exception e){
			log.error(e);
			processingExceptions.put(file, e);
			if(isStopOnFirstFileProcessingException())
				throw e;
		}
	}
	
	public boolean processingExceptionsOccurred(){
		return !processingExceptions.isEmpty();
	}
	
	public Set<File> getFilesWithProcessingExceptions(){
		return processingExceptions.keySet();
	}
	
	protected boolean readDirectoryContents(File directory){
		return true;
	}
	
	protected boolean readFilesInDirectory(File directory){
		return true;
	}
	
	protected boolean readSubdirectoriesInDirectory(File directory){
		return true;
	}
	
	protected void processFile(File file) throws Exception{};
	
	protected void processDirectory(File file) throws Exception{};

}
