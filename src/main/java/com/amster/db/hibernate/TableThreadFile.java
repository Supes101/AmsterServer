package com.amster.db.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;


// Generated Sep 8, 2016 3:44:55 PM by Hibernate Tools 3.4.0.CR1

/**
 * TableThreadFile generated by hbm2java
 */

@Entity
@Table(name = "table_thread_file")
public class TableThreadFile implements java.io.Serializable {

	@Id @GeneratedValue
	@Column(name = "_id")
	private Integer id;
	
	@Column(name = "thread_id")
	private String threadId;
	
	@Column(name = "thread_file")
	private String threadFile;
	
	@JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_info2log_file")
	private TableLogFile logFile;


	public TableThreadFile() {
	}

	public TableThreadFile(String threadId, String threadFile, TableLogFile log_file) {
		this.threadId = threadId;
		this.threadFile = threadFile;
		this.logFile = log_file;
		
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getThreadId() {
		return this.threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getThreadFile() {
		return this.threadFile;
	}

	public void setThreadFile(String threadFile) {
		this.threadFile = threadFile;
	}

	public TableLogFile getLogFile(){
		return this.logFile;
	}
	
	public void setLogFile(TableLogFile log_file){
		this.logFile=log_file;
	}

}
