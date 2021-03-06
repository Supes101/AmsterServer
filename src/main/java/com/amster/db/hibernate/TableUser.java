package com.amster.db.hibernate;

// Generated Sep 8, 2016 3:44:55 PM by Hibernate Tools 3.4.0.CR1

/**
 * TableUser generated by hbm2java
 */
public class TableUser implements java.io.Serializable {

	private Integer id;
	private String userName;
	private String password;
	private String email;
	private String status;
	private Integer admin;
	private Integer valid;

	public TableUser() {
	}

	public TableUser(String userName, String password, String email,
			String status, Integer admin, Integer valid) {
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.status = status;
		this.admin = admin;
		this.valid = valid;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getAdmin() {
		return this.admin;
	}

	public void setAdmin(Integer admin) {
		this.admin = admin;
	}

	public Integer getValid() {
		return this.valid;
	}

	public void setValid(Integer valid) {
		this.valid = valid;
	}

}
