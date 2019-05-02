package com.example.heroku;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file, Map<String, Object> model) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
		
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
			
			String sql = "CREATE TABLE db (" + cols + ")";
		    stmt.executeUpdate(sql);
			
			for (CSVRecord csvRecord : parser) {
				sql = "INSERT INTO db VALUES(" + csvRecord.toString() + ")";
				stmt.execute(sql);
			}
			
			  ResultSet rs = stmt.executeQuery("SELECT * FROM db");

			  ArrayList<String> output = new ArrayList<String>();
			  while (rs.next()) {
				output.add("Read from DB: " + rs);
			  }

			  model.put("records", output);
			  return "db_old";
			} catch (Exception e) {
			  model.put("message", e.getMessage());
			  return "error";
			}

    }
	
		public void write(MultipartFile fl, Path pth) throws Exception{
		Path filepath = Paths.get(pth.toString(), fl.getOriginalFilename());
		fl.transferTo(filepath);
	

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                .filter(path -> !path.equals(this.rootLocation))
                .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}