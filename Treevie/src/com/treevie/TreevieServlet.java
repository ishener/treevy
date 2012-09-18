package com.treevie;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class TreevieServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
//		resp.setContentType("text/plain");
//		Date nowdate = new Date();
//		resp.getWriter().println(nowdate);
		
		Question newq = new Question ();
		
//		newq.addWrongAnswer("answer 1624");
//		newq.addWrongAnswer("answer 1925");
//		newq.persist(true);
		

		req.setAttribute("question", newq);
		try { 
			getServletContext().getRequestDispatcher("/show-main-question.jsp").forward(req, resp); 
		} catch (ServletException e) {
			System.out.println (e.getMessage());
		}
	}
}
