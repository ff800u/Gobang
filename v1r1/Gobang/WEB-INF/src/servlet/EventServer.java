package servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import log.LogRecoder;

import event.EventManager;

public class EventServer extends HttpServlet {
	private static final long serialVersionUID = -6124407964372523655L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// ½øÐÐÂÖÑ¯
		Map<String, Object> ret = new HashMap<String, Object>();
		LogRecoder.EVENT.debug("page " + req.getSession().getId()
				+ " try to get events. ");
		ret.put("eventList",
				EventManager.INSTANCE.getEvents(req.getRequestedSessionId()
						+ "_" + req.getParameter("pageid")));
		resp.getWriter().write(JsonUtil.toJsonString(ret));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// ×¢²áÂÖÑ¯
		EventManager.INSTANCE.registerPage(req.getRequestedSessionId() + "_"
				+ req.getParameter("pageid"));
		resp.getWriter().write("sucess");
		resp.getWriter().close();
	}
}
