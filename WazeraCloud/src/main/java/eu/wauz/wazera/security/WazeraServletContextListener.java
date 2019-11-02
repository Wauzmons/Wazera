package eu.wauz.wazera.security;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WazeraServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("ServletContextListener initialized");
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("ServletContextListener destroyed");
	}

}