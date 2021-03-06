package com.amster.db.hibernate;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

// Generated Sep 8, 2016 3:44:55 PM by Hibernate Tools 3.4.0.CR1

/**
 * TableException generated by hbm2java
 */

@Entity
@Table(name = "table_exception")
public class TableException implements java.io.Serializable {

	@Id @GeneratedValue
	@Column(name = "_id")
	private Integer id;
	
	@Column(name = "exception")
	private String exception;
	
	@Column(name = "stack_trace")
	private String stackTrace;
	
	@Column(name = "exception_count")
	private Integer exceptionCount;
	
	@JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exception2log_file")
	private TableLogFile logFile;

	@OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY, mappedBy = "exception")
	private Set<TableExceptionTimes> exceptionTimes = new HashSet<TableExceptionTimes>(0);

	public TableException() {
	}

	public TableException(String exception, String stackTrace,
			 Integer exceptionCount, TableLogFile log_file) {
		this.exception = exception;
		this.stackTrace = stackTrace;
		this.exceptionCount = exceptionCount;
		this.logFile = log_file;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getException() {
		return this.exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getStackTrace() {
		return this.stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}


	public Integer getExceptionCount() {
		return this.exceptionCount;
	}

	public void setExceptionCount(Integer exceptionCount) {
		this.exceptionCount = exceptionCount;
	}

	public TableLogFile getLogFile(){
		return this.logFile;
	}
	
	public void setLogFile(TableLogFile log_file){
		this.logFile=log_file;
	}
	
	public Set<TableExceptionTimes> getExceptionTimes() {
		return this.exceptionTimes;
	}

	public void setExceptionTimes(Set<TableExceptionTimes> etimes) {
		this.exceptionTimes = etimes;
	}

}
