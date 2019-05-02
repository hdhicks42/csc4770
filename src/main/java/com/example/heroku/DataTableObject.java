package com.example.heroku;

import java.util.List;

public class DataTableObject {
	
	int num_records;
	String columns;
	List<Object> records;
	
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
	
	public List<Object> getRecords () {
		return records;
	}
	
	public void setRecords(List<Object> rec){
		records = rec;
	}
}