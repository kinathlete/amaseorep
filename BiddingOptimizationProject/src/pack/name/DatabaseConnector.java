package pack.name;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;



public class DatabaseConnector {
	
	private static DataSource ds = new DataSource();
    
    static {
    	PoolProperties p = new PoolProperties();
        p.setUrl("jdbc:mysql://database-2.cf17sjm1vywd.us-east-2.rds.amazonaws.com:3306/amaseodb");
        p.setDriverClassName("com.mysql.cj.jdbc.Driver");
        p.setUsername("amaseodb");
        p.setPassword("8zeichen");
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(60);
        p.setInitialSize(20);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(6000);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setMaxIdle(60);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;"
                + "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");
        ds.setPoolProperties(p);
    }
     
    public static Connection getConnection() throws SQLException {
    	Connection con =  ds.getConnection();
    	return con;
    }
     
    public DatabaseConnector(){ }
	


}
