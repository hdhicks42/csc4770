package com.example.heroku;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.csv.*;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.Map;

public class DataServlet extends HttpServlet {
 
 private static final long serialVersionUID = 1L;
 
 public DataServlet() {
  super();
 }
 
 @GetMapping("/data")
 public String doGet(HttpServletRequest request,
	  HttpServletResponse response, Map<String, Object> model) throws ServletException, IOException {
		  response.setContentType("application/json");
		  List<String> list = DataService.getData();
		  PrintWriter out = response.getWriter();
		   
		  DataTableObject dataTableObject = new DataTableObject();
		  dataTableObject.setRecords(list);
		   
		  Gson gson = new GsonBuilder().setPrettyPrinting().create();
		  String json = gson.toJson(dataTableObject);
		  out.print(json);
		  
		  return "data";
 }
 
 protected void doPost(HttpServletRequest request,
	HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
 }
 
}