package com.example.heroku;

import java.io.IOException;
import java.util.stream.Collectors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.apache.commons.csv.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.CommandLineRunner;
import org.apache.commons.dbcp2.BasicDataSource;
import java.net.*;
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
import java.sql.DriverManager;

@Controller
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class FileUploadController {

    private final StorageService storageService;
	public File new_file;

	@Value("${spring.datasource.url}")
	private String dbUrl;

	@Autowired
	private static DataSource dataSource;
	
	  public static void main(String[] args) throws Exception {
		SpringApplication.run(FileUploadController.class, args);
	  }

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
            RedirectAttributes redirectAttributes) 
			throws Exception{
				
		storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
			
			return  "redirect:/";
	
    }
	
	public void write(MultipartFile fl, Path pth) throws Exception{
		Path filepath = Paths.get(pth.toString(), fl.getOriginalFilename());
		fl.transferTo(filepath);
	
	}
	
	@GetMapping("/db")
	 public String dataBaseHandler(Model model){
	

		try (Connection connection = dataSource.getConnection()) {
			 Statement stmt = connection.createStatement();
			 
			 
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
			
			String sql = "CREATE TABLE db (" + heads + ")";
		    stmt.executeUpdate(sql);
			
			for (CSVRecord csvRecord : parser) {
				sql = "INSERT INTO db VALUES(" + csvRecord.toString() + ")";
				stmt.execute(sql);
			}
			
			  ResultSet rs = stmt.executeQuery("SELECT * FROM db");


			  ArrayList<String> output = new ArrayList<String>();
			  
			/*  while (rs.next()) {
				output.add(rs);
			  }*/
			  
			  model.addAttribute("records", rs);

			 
			  return "db";
			} catch (Exception e) {
			  
			  return "error";
			}

	}


@Configuration
public class MainConfig {

    @Bean
    public BasicDataSource dataSource() throws URISyntaxException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(dbUrl);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);

        return basicDataSource;
    }
}
	  
  
    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

	private static Connection getConnection() throws URISyntaxException, SQLException {
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

		return DriverManager.getConnection(dbUrl, username, password);
	}

	
  @RequestMapping("/")
  String index() {
    return "index";
  }

  


    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
           // storageService.deleteAll();
            storageService.init();
        };
    }
}