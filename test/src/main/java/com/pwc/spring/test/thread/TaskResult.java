package com.pwc.spring.test.thread;

public class TaskResult {
	public TaskResult(Boolean isDone, String nextQuery) {
		super();
		this.isDone = isDone;
		this.nextQuery = nextQuery;
	}
	public TaskResult(){
		
	}
	private Boolean isDone;
	
	public Boolean getIsDone() {
		return isDone;
	}
	public void setIsDone(Boolean isDone) {
		this.isDone = isDone;
	}
	public String getNextQuery() {
		return nextQuery;
	}
	public void setNextQuery(String nextQuery) {
		this.nextQuery = nextQuery;
	}
	private String nextQuery;
}
