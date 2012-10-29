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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class AddQuestion extends HttpServlet {
	/*
	 * this request handles new question submission
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Question newq = null;
		if ( req.getParameter("key") != null ) {
			// it was an edit. retrieve the question and update
			try {
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				newq = new Question (datastore.get( KeyFactory.stringToKey(req.getParameter("key")) ));
				newq.emptyWrongAnswers();
			} catch (EntityNotFoundException e) {
				System.out.println("why u no give good key for edit question on submit?");
			}
		} else {
			newq = new Question (req.getParameter("question"));
		}
		resp.setContentType("text/plain");
		
		
		newq.setRightAnswer(req.getParameter("right"));
		newq.addWrongAnswer(req.getParameter("wrong-1"));
		newq.addWrongAnswer(req.getParameter("wrong-2"));
		newq.addWrongAnswer(req.getParameter("wrong-3"));
		newq.setLevel( Long.parseLong (req.getParameter("level")));
		newq.setSeries(req.getParameter("series"));
		newq.persist();
		
		resp.getWriter().println("Successfully persisted new question.<br/><a href=\"/add-question\">Back to list</a>");
	}
	
	/*
	 * various admin stuff
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		if ( req.getParameter("action") != null ) {
			String webKey = req.getParameter("key");
			if ( req.getParameter("action").equals("delete") ) {
				Question.delete(KeyFactory.stringToKey(webKey));
			} else { // edit
				try {
					Question que = new Question (datastore.get( KeyFactory.stringToKey(webKey) ));
					req.setAttribute("editque", que);
					try { 
						getServletContext().getRequestDispatcher("/admin/add-question.jsp").forward(req, resp); 
					} catch (ServletException e) {
						System.out.println (e.getMessage());
					}
				} catch (EntityNotFoundException e) {
					System.out.println("why u no give good key for edit question?");
				}
			}
		}  else { // display all questions
			// TODO: pager for ques
			List<Question> questions = new ArrayList<Question>();
			
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
}

