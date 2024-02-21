package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;


import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class JpaConfig {

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        emfb.setPersistenceXmlLocation("classpath:META-INF/persistence.xml");
        emfb.afterPropertiesSet();

        return emfb.getObject();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        Properties connProps = new Properties();
        connProps.setProperty("user","postgres");
        connProps.setProperty("password","abcd");
        connProps.setProperty("encrypt","false");
        connProps.setProperty("socketFactory","com.google.cloud.sql.postgres.SocketFactory");
        connProps.setProperty("driverClassName", "org.postgresql.Driver");
        connProps.setProperty("socketFactoryConstructorArg",
                "chess-412709:us-central1:chess-instance?ipTypes=PUBLIC");
        connProps.setProperty("url", "jdbc:postgresql://34.135.182.222:5432/chess");
        ds.setConnectionProperties(connProps);
        return ds;
    }
}

/*
com.google.cloud.sql.postgres
 * INSTANCE_CONNECTION_NAME: <PROJECT-ID>:<INSTANCE-REGION>:INSTANCE-NAME>
INSTANCE_UNIX_SOCKET: /cloudsql/<PROJECT-ID>:<INSTANCE-REGION>:INSTANCE-NAME>
INSTANCE_HOST: '127.0.0.1'
DB_PORT: 5432
DB_USER: <YOUR_DB_USER_NAME>
DB_IAM_USER: <YOUR_DB_IAM_USER_NAME>
DB_PASS: <YOUR_DB_PASSWORD>
DB_NAME: <YOUR_DB_NAME>
 */

 /*
  * String jdbcUrl = "jdbc:postgresql:///<DATABASE_NAME>?" 
    + "cloudSqlInstance=<INSTANCE_CONNECTION_NAME>" 
    + "&socketFactory=com.google.cloud.sql.postgres.SocketFactory" 
    + "&user=<POSTGRESQL_USER_NAME>" 
    + "&password=<POSTGRESQL_USER_PASSWORD>";
  */