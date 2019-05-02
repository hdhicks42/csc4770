package com.example.heroku;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.*;

public class DataService{
	
	public static List<CSVRecord> getData(){
		
		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT ALL FROM db");

			ArrayList<String> output = new ArrayList<String>();
			
			while (rs.next()) {
				output.add(rs);
			}
			
		} catch (Exception e) {
			  return "error";
		}
		
		return data;
	}
	

}