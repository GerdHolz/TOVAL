package de.invation.code.toval.poiutil.excel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetProtection;

public class SheetProtectionSettings {
	
	public static final boolean DEFAULT_SELECT_LOCKED_CELLS 	= false;
	public static final boolean DEFAULT_SELECT_UNKLOCKED_CELLS 	= false;
	public static final boolean DEFAULT_FORMAT_CELLS			= true;
	public static final boolean DEFAULT_FORMAT_COLUMNS			= true;
	public static final boolean DEFAULT_FORMAT_ROWS				= true;
	public static final boolean DEFAULT_INSERT_COLUMNS			= true;
	public static final boolean DEFAULT_INSERT_ROWS				= true;
	public static final boolean DEFAULT_INSERT_HYPERLINKS		= true;
	public static final boolean DEFAULT_DELETE_COLUMNS			= true;
	public static final boolean DEFAULT_DELETE_ROWS				= true;
	public static final boolean DEFAULT_SORT					= false;
	public static final boolean DEFAULT_AUTO_FILTER				= false;
	public static final boolean DEFAULT_PIVOT_TABLES			= true;
	public static final boolean DEFAULT_OBJECTS					= true;
	public static final boolean DEFAULT_SCENARIOS				= true;
	
	private boolean selectLockedCells 	= DEFAULT_SELECT_LOCKED_CELLS;
	private boolean selectUnlockedCells = DEFAULT_SELECT_UNKLOCKED_CELLS;
	private boolean formatCells 		= DEFAULT_FORMAT_CELLS;
	private boolean formatColumns 		= DEFAULT_FORMAT_COLUMNS;
	private boolean formatRows 			= DEFAULT_FORMAT_ROWS;
	private boolean insertColumns 		= DEFAULT_INSERT_COLUMNS;
	private boolean insertRows 			= DEFAULT_INSERT_ROWS;
	private boolean insertHyperlinks 	= DEFAULT_INSERT_HYPERLINKS;
	private boolean deleteColumns 		= DEFAULT_DELETE_COLUMNS;
	private boolean deleteRows 			= DEFAULT_DELETE_ROWS;
	private boolean sort 				= DEFAULT_SORT;
	private boolean autoFilter 			= DEFAULT_AUTO_FILTER;
	private boolean pivotTables 		= DEFAULT_PIVOT_TABLES;
	private boolean objects 			= DEFAULT_OBJECTS;
	private boolean scenarios 			= DEFAULT_SCENARIOS;
	
	public boolean isSelectLockedCells() {
		return selectLockedCells;
	}
	public void setSelectLockedCells(boolean selectLockedCells) {
		this.selectLockedCells = selectLockedCells;
	}
	public boolean isSelectUnlockedCells() {
		return selectUnlockedCells;
	}
	public void setSelectUnlockedCells(boolean selectUnlockedCells) {
		this.selectUnlockedCells = selectUnlockedCells;
	}
	public boolean isFormatCells() {
		return formatCells;
	}
	public void setFormatCells(boolean formatCells) {
		this.formatCells = formatCells;
	}
	public boolean isFormatColumns() {
		return formatColumns;
	}
	public void setFormatColumns(boolean formatColumns) {
		this.formatColumns = formatColumns;
	}
	public boolean isFormatRows() {
		return formatRows;
	}
	public void setFormatRows(boolean formatRows) {
		this.formatRows = formatRows;
	}
	public boolean isInsertColumns() {
		return insertColumns;
	}
	public void setInsertColumns(boolean insertColumns) {
		this.insertColumns = insertColumns;
	}
	public boolean isInsertRows() {
		return insertRows;
	}
	public void setInsertRows(boolean insertRows) {
		this.insertRows = insertRows;
	}
	public boolean isInsertHyperlinks() {
		return insertHyperlinks;
	}
	public void setInsertHyperlinks(boolean insertHyperlinks) {
		this.insertHyperlinks = insertHyperlinks;
	}
	public boolean isDeleteColumns() {
		return deleteColumns;
	}
	public void setDeleteColumns(boolean deleteColumns) {
		this.deleteColumns = deleteColumns;
	}
	public boolean isDeleteRows() {
		return deleteRows;
	}
	public void setDeleteRows(boolean deleteRows) {
		this.deleteRows = deleteRows;
	}
	public boolean isSort() {
		return sort;
	}
	public void setSort(boolean sort) {
		this.sort = sort;
	}
	public boolean isAutoFilter() {
		return autoFilter;
	}
	public void setAutoFilter(boolean autoFilter) {
		this.autoFilter = autoFilter;
	}
	public boolean isPivotTables() {
		return pivotTables;
	}
	public void setPivotTables(boolean pivotTables) {
		this.pivotTables = pivotTables;
	}
	public boolean isObjects() {
		return objects;
	}
	public void setObjects(boolean objects) {
		this.objects = objects;
	}
	public boolean isScenarios() {
		return scenarios;
	}
	public void setScenarios(boolean scenarios) {
		this.scenarios = scenarios;
	}
	
	public void apply(CTSheetProtection sheetProtection){
		apply(this, sheetProtection);
	}
	
	public static void applyDefaultSettings(CTSheetProtection sheetProtection){
		apply(defaultSettings(), sheetProtection);
	}
	
	public static void apply(SheetProtectionSettings settings, CTSheetProtection sheetProtection){
		sheetProtection.setSelectLockedCells(	settings.isSelectLockedCells());
		sheetProtection.setSelectUnlockedCells(	settings.isSelectUnlockedCells());
		sheetProtection.setFormatCells(			settings.isFormatCells());
		sheetProtection.setFormatColumns(		settings.isFormatColumns());
		sheetProtection.setFormatRows(			settings.isFormatRows());
		sheetProtection.setInsertColumns(		settings.isInsertColumns());
		sheetProtection.setInsertRows(			settings.isInsertRows());
		sheetProtection.setInsertHyperlinks(	settings.isInsertHyperlinks());
		sheetProtection.setDeleteColumns(		settings.isDeleteColumns());
		sheetProtection.setDeleteRows(			settings.isDeleteRows());
		sheetProtection.setSort(				settings.isSort());
		sheetProtection.setAutoFilter(			settings.isAutoFilter());
		sheetProtection.setPivotTables(			settings.isPivotTables());
		sheetProtection.setObjects(				settings.isObjects());
		sheetProtection.setScenarios(			settings.isScenarios());
	}
	
	public static SheetProtectionSettings defaultSettings(){
		return new SheetProtectionSettings();
	}
	
	

}
