package com.patienttouch.hibernate;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * This table keeps track of when was a particular api last accessed by the user. We have a functionality in the
 * application where in the most recent updates should high lighted so that the user can view them.
 * 
 * @author Ishwardeepsingh
 *
 */
@Entity
public class UserApiInvocationLog {
	private int id;
	private User user;
	private UserApi api;
	private Date lastAccessTime;
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@OneToOne
	@JoinColumn(name="userid")
	public User getUser() {
		return user;
	}
	public void setUser(User userid) {
		this.user = userid;
	}
	
	@Enumerated(EnumType.STRING)
	public UserApi getApi() {
		return api;
	}
	public void setApi(UserApi api) {
		this.api = api;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastAccessTime() {
		return lastAccessTime;
	}
	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}	
}
