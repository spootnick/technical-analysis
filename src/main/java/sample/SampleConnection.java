package sample;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class SampleConnection {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		 Connection connection = DriverManager.getConnection("jdbc:hsqldb:file:d:/java/hsqldb/test", "SA", "");

		 Statement statement = connection.createStatement();
		 
		 
		 
		 int i = statement.executeUpdate("insert into t (a,b) values (3,3)");
		 
		 System.out.println(i);
		 
		 statement.execute("shutdown");
	}

}
