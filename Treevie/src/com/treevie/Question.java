package com.treevie;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

public class Question {
	private String question;
	private final List<Answer> wrongAnswers = new ArrayList<Answer>();
	private Answer rightAnswer;
	Calendar tempDate = new GregorianCalendar(2011, 1, 1);
	private Date lastDisplayed = tempDate.getTime();
	
	public  Question () { 
		// regular constructor returns random question
		Random randomGenerator = new Random();
		double rand = randomGenerator.nextDouble();
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		FilterOperator op = FilterOperator.GREATER_THAN_OR_EQUAL;
		int retry = 3;
		
		while (retry != 0) {
		
			Filter randomFilter = new FilterPredicate ( "rand", op, rand );
			Query q = new Query("Question")
							.setFilter(randomFilter)
							.addSort("rand", SortDirection.ASCENDING)
							.addSort("lastDisplayed", SortDirection.ASCENDING);
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(1));
			
			
			if (!result.isEmpty()) {
				System.out.println(rand);
				this.question = (String) result.get(0).getProperty("question");
				
				try {
					this.rightAnswer = new Answer ((EmbeddedEntity) result.get(0).getProperty("rightAnswer"));
				} catch (NullPointerException e) {
					this.rightAnswer = null;
				}
				
				// now get all the wrong answers and populate them
				List<EmbeddedEntity> embeddedAnswers = (List<EmbeddedEntity>) result.get(0).getProperty("wrongAnswer");
				for (EmbeddedEntity wrongAnswer : embeddedAnswers) {
					this.wrongAnswers.add( new Answer(wrongAnswer) ); 
				}
				retry = 0;
			} else {
				// we couldn't find a question with a random number greater than our rand.
				// no problem. just reverse the operator, and try again
				op = FilterOperator.LESS_THAN_OR_EQUAL;
				System.out.println("Failed finding " + rand + ". retying...");
				retry--;
			}
			
		}
	}
	
	/*
	 * this constructor is for getting a Question object from a fetched entity
	 */
	public Question (Entity entity) {
		this.question = (String) entity.getProperty("question");
		try {
			this.rightAnswer = new Answer ((EmbeddedEntity) entity.getProperty("rightAnswer"));
		} catch (NullPointerException e) {
			this.rightAnswer = null;
		}
		
		Object objectAnswers = entity.getProperty("wrongAnswer");
		if (objectAnswers instanceof ArrayList) {
			List<EmbeddedEntity> embeddedAnswers = (List<EmbeddedEntity>) objectAnswers;
			for (EmbeddedEntity wrongAnswer : embeddedAnswers) {
				this.wrongAnswers.add( new Answer(wrongAnswer) );
			}
		}
	}
	
	public Question (String arg) {
		this.question = arg;
	}
	
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String arg) {
		this.question = arg;
	}
	public Answer getRightAnswer () {
		return this.rightAnswer;
	}
	public void setRightAnswer (String ans) {
		this.rightAnswer = new Answer (ans, 0);
	}
	
	public void addWrongAnswer ( String ans ) {
		wrongAnswers.add (new Answer(ans, 0));
	}
	public List<Answer> getWrongAnswers () {
		return this.wrongAnswers;
	}
	
	
	public Date getLastDisplayed() {
		return lastDisplayed;
	}

	public void setLastDisplayed(Date lastDisplayed) {
		this.lastDisplayed = lastDisplayed;
	}

	public Boolean persist () {
		return this.persist(false);
	}
	public Boolean persist (Boolean anew) {
		Boolean success = false;
		
		if (anew) {  // insert a new question
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Entity questionEntity = new Entity("Question");
			questionEntity.setProperty("question", this.question);
			questionEntity.setProperty("lastDisplayed", this.lastDisplayed);
			
			Random randomGenerator = new Random();
			double rand = randomGenerator.nextDouble();
			questionEntity.setProperty("rand", rand);
			
			questionEntity.setProperty ("rightAnswer", this.rightAnswer.embed() );
			
			List<EmbeddedEntity> embeddedAnswers = new ArrayList<EmbeddedEntity> ();
			
			// convert the answers to embeddable entities
			for (Answer temp : this.wrongAnswers) {
				embeddedAnswers.add( temp.embed() );
			}
			questionEntity.setProperty ("wrongAnswer", embeddedAnswers);
			
			// TODO: handle some rare exceptions
			datastore.put(questionEntity);
		}
		return success;
	}
	
	
	
	public class Answer {
		private String answer;
		private long count;
		
		public Answer (EmbeddedEntity embedded) {
			// this function is for unembedding answers
			this.answer = (String) embedded.getProperty("answer");
			this.count = (Long) embedded.getProperty("count");
		}
		
		public Answer (String a, long c) {
			this.answer = a;
			this.count = c;
		}
		public String getAnswer() {
			return this.answer;
		}
		public long getCount() {
			return this.count;
		}
		
		public EmbeddedEntity embed () {
			EmbeddedEntity embedAnswer = new EmbeddedEntity();
			embedAnswer.setProperty( "answer" , this.getAnswer());
			embedAnswer.setProperty( "count" , this.getCount());
			return embedAnswer;
		}
		
	}
	
}
