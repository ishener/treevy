package com.treevie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class AddQuestion extends HttpServlet {
	/*
	 * this request handles new question submission
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		
		Question newq = new Question (req.getParameter("question"));
		newq.setRightAnswer(req.getParameter("right"));
		newq.addWrongAnswer(req.getParameter("wrong-1"));
		newq.addWrongAnswer(req.getParameter("wrong-2"));
		newq.addWrongAnswer(req.getParameter("wrong-3"));
		newq.persist(true);
		
		resp.getWriter().println("Successfully persisted new question");
	}
	
	/*
	 * various admin stuff
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// TODO: pager for ques
		List<Question> questions = new ArrayList<Question>();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Question");
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(20));
		for (Entity result : results) {
			questions.add( new Question (result) );
		}
		
		req.setAttribute("questions", questions);
		try { 
			getServletContext().getRequestDispatcher("/admin/view-questions.jsp").forward(req, resp); 
		} catch (ServletException e) {
			System.out.println (e.getMessage());
		}
	}
}

