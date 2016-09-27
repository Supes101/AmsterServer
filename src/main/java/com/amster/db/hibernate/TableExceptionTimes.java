package com.amster.db.hibernate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

// Generated Sep 8, 2016 3:44:55 PM by Hibernate Tools 3.4.0.CR1

/**
 * TableExceptionTimes generated by hbm2java
 */
public class TableExceptionTimes implements java.io.Serializable {

	private Integer id;
	private String exceptionTime;
	@JsonIgnore
	private TableException exception;

	public TableExceptionTimes() {
	}

	public TableExceptionTimes(String exceptionTime,
			TableException exception) {
		this.exceptionTime = exceptionTime;
		this.exception = exception;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getExceptionTime() {
		return this.exceptionTime;
	}

	public void setExceptionTime(String exceptionTime) {
		this.exceptionTime = exceptionTime;
	}

	public TableException getException(){
		return this.exception;
	}
	
	public void setException(TableException e){
		this.exception=e;
	}

}