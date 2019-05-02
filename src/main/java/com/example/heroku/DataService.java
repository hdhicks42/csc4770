package com.example.heroku;

import java.util.ArrayList;
import java.util.List;

public class DataService{
	List<Object> data;
	
	public static List<Object> getData(){
		return data;
	}
	
	public void setData(List<Object> new_data){
		data = new_data;
	}

}