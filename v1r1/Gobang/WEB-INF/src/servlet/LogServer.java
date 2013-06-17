package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import log.LogRecoder;

public class LogServer extends HttpServlet {
	private static final long serialVersionUID = 6959201340279430266L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String level = req.getParameter("level");
		String data = req.getParameter("data");
		if ("debug".equals(level)) {
			LogRecoder.WEB.debug(data);
		} else if ("warn".equals(level)) {
			LogRecoder.WEB.warn(data);
		} else if ("error".equals(level)) {
			LogRecoder.WEB.error(data);
		}
	}
}
