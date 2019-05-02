package com.example.heroku;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.*;

public class DataService{
	List<Object> data;
	
	public static List<CSVRecord> getData(){
		return data;
	}
	
	public void setData(List<CSVRecord> new_data){
		data = new_data;
	}

}