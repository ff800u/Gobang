package servlet;

import gobang.GobangManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletInitial implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent servletcontextevent) {
		GobangManager.INSTANCE.initialized();
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletcontextevent) {
		GobangManager.INSTANCE.destroyed();
	}
}
