package com.example.heroku;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.beans.factory.annotation.Autowired;

public class DataService{
	
	@Autowired
	private  static DataSource dataSource;
	
	public static List<String> getData(){
		ArrayList<String> output = new ArrayList<String>();

		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT ALL FROM db");

			
			
			while (rs.next()) {
				output.add(rs.toString());
			}
			
		} catch (Exception e) {
			  
		}
		
		return output;
	}
	

}