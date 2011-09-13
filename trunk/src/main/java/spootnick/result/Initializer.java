package spootnick.result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

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

		// statement.execute("drop table result");

		try {
			execute(statement,read("/check.sql"));
			log.info("table check passed");
		} catch (SQLException e) {
			execute(statement,read("/create.sql"));
			log.info("create executed");
		}

		statement.execute("shutdown");

	}

	private void execute(Statement statement, List<String> sqls) throws SQLException {
		for (String sql : sqls) {
			statement.execute(sql);
		}
	}

	private List<String> read(String name) {
		try {
			InputStream is = getClass().getResourceAsStream(name);
			List<String> ret = new LinkedList<String>();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				ret.add(line);
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException("couldn't read: " + name, e);
		}
	}
}
