package com.example.heroku;

import java.io.IOException;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.apache.commons.csv.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Object;


@Controller
public class FileUploadController {

    private final StorageService storageService;
	
	 @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/upload")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        return "upload";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes, Map<String, Object> model) {
				
		storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

		  try (Connection connection = dataSource.getConnection()) {
			 Statement stmt = connection.createStatement();
			 //stmt.executeUpdate("CREATE TABLE db (obs_id int, site_id int, datetime varchar, forecast_id int, value int )");
		
			 write(file, storageService.load(file.getOriginalFilename()));
			 
			 File new_file = new File (file.getOriginalFilename());
			 CSVParser parser = CSVParser.parse(new_file, StandardCharsets.US_ASCII, CSVFormat.EXCEL);
	
			
			Map<String,Integer> headers = parser.getHeaderMap();
			Set<String> col = headers.keySet();
			
			Iterator<String> iter = col.iterator();
			
			String [] cols = new String [20];
			int i = 0;
			while (iter.hasNext()){
				String curr = iter.next();
				cols[i] = curr;
			}
			String heads = StringUtils.arrayToCommaDelimitedString(cols);
			
			String sql = "CREATE TABLE db (" + cols + ")";
		    stmt.executeUpdate(sql);
			
				
			  
			  ResultSet rs = stmt.executeQuery("SELECT * FROM db");

			  ArrayList<String> output = new ArrayList<String>();
			  while (rs.next()) {
				output.add("Read from DB: " + rs);
			  }

			  model.put("records", output);
			  return "db";
			} catch (Exception e) {
			  model.put("message", e.getMessage());
			  return "error";
			}

        
    }
	
	public void write(MultipartFile fl, Path pth) throws Exception{
		Path filepath = Paths.get(pth.toString(), fl.getOriginalFilename());
		fl.transferTo(filepath);
	
	}

	 @Bean
	  public DataSource dataSource() throws SQLException {
		if (dbUrl == null || dbUrl.isEmpty()) {
		  return new HikariDataSource();
		} else {
		  HikariConfig config = new HikariConfig();
		  config.setJdbcUrl(dbUrl);
		  return new HikariDataSource(config);
		}
	  }
  
    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}