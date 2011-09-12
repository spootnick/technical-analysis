package spootnick.result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Initializer {

	private Logger log = LoggerFactory.getLogger(Initializer.class); 
	
	private String url;
	private String password;
	private String username;

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@PostConstruct
	public void init() throws SQLException {

		Connection connection = DriverManager.getConnection(url, username,
				password);

		Statement statement = connection.createStatement();

		//statement.execute("drop table result");
		
		try {
			statement.execute(read("/check.sql"));
			log.info("table check passed");
		} catch (SQLException e) {
			statement.execute(read("/create.sql"));
			log.info("create executed");
		}

		statement.execute("shutdown");

	}

	private String read(String name) {
		try {
			InputStream is = getClass().getResourceAsStream(name);

			return new BufferedReader(new InputStreamReader(is)).readLine();
		} catch (IOException e) {
			throw new RuntimeException("couldn't read: " + name, e);
		}
	}
}
