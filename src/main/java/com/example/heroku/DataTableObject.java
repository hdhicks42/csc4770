package com.example.heroku;

import java.util.List;
import org.apache.commons.csv.*;

public class DataTableObject {
	
	int num_records;
	String columns;
	List<CSVRecord> records;
	
	public int getNumRecords(){
		return num_records;
	}
	
	public void setNumRecords(int num){
		num_records = num;
	}
	
	public String getColumns() {
		return columns;
	}
	
	public void setColumns(String col) {
		columns = col;
	}
	
	public List<CSVRecord> getRecords () {
		return records;
	}
	
	public void setRecords(List<CSVRecord> rec){
		records = rec;
	}
}