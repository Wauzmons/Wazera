package eu.wauz.wazera;

import javax.faces.webapp.FacesServlet;
import javax.servlet.Servlet;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"eu.wauz"})
public class WazeraApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(WazeraApplication.class, args);
	}
	
	@Bean
	public ServletRegistrationBean<Servlet> servletRegistrationBean() {
		FacesServlet facesServlet = new FacesServlet();
		ServletRegistrationBean<Servlet> servletRegistrationBean = new ServletRegistrationBean<Servlet>(facesServlet, "*.xhtml");
		return servletRegistrationBean;
	}
	
    @Bean
    @Autowired
	public DataSource docsDataSource() {
		try {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
			dataSource.setUsername("root");
			dataSource.setPassword("root");
			dataSource.setUrl("jdbc:mariadb://localhost:3306/wazera");
			return dataSource;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
