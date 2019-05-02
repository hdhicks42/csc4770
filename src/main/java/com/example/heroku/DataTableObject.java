package com.example.heroku;

import java.util.List;

public class DataTableObject {
	
	int num_records;
	String columns;
	List<String> records;
	
	public int getNumRecords(){
		return num_records;
	}
	
	public void setNumRecords(int num){
		num_records = num;
	}
	
	public String getColumns() {
		return columns;
	}
	
	public void setColumns(int col) {
		columns = col;
	}
	
	public List<String> getRecords () {
		return records;
	}
	
	public void setRecords(List<String> rec){
		records = rec;
	}
}