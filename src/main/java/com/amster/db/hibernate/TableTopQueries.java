package com.amster.db.hibernate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

// Generated Sep 8, 2016 3:44:55 PM by Hibernate Tools 3.4.0.CR1

/**
 * TableTopQueries generated by hbm2java
 */
public class TableTopQueries implements java.io.Serializable {

	private Integer id;
	private String querySql;
	private Float elapsedTime;
	private String queryTime;
	private Integer queryOrder;
	
	@JsonIgnore
	private TableLogFile logFile;

	public TableTopQueries() {
	}

	public TableTopQueries(String querySql, Float elapsedTime,
			String queryTime, Integer queryOrder, TableLogFile log_file) {
		this.querySql = querySql;
		this.elapsedTime = elapsedTime;
		this.queryTime = queryTime;
		this.queryOrder = queryOrder;
		this.logFile = log_file;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQuerySql() {
		return this.querySql;
	}

	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}

	public Float getElapsedTime() {
		return this.elapsedTime;
	}

	public void setElapsedTime(Float elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public String getQueryTime() {
		return this.queryTime;
	}

	public void setQueryTime(String queryTime) {
		this.queryTime = queryTime;
	}

	public Integer getQueryOrder() {
		return this.queryOrder;
	}

	public void setQueryOrder(Integer queryOrder) {
		this.queryOrder = queryOrder;
	}

	
	public TableLogFile getLogFile(){
		return this.logFile;
	}
	
	public void setLogFile(TableLogFile log_file){
		this.logFile=log_file;
	}


}
