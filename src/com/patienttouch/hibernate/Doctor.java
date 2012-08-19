package com.patienttouch.hibernate;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;


@Entity
public class Doctor {
    private int doctorid;
    private String firstName;
    private String lastName;
    private String nickName;
    private int del;
    private Practice practice;
    private List<Office> office;
    
    @Id
	@GeneratedValue
	public int getDoctorid() {
		return doctorid;
	}
	public void setDoctorid(int doctorid) {
		this.doctorid = doctorid;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getDel() {
		return del;
	}
	public void setDel(int del) {
		this.del = del;
	}
	
	@ManyToOne
	@JoinColumn(name="practiceid")
	public Practice getPractice() {
		return practice;
	}
	public void setPractice(Practice practice) {
		this.practice = practice;
	}
	
	@ManyToMany
	@JoinTable(name="PracticeOfficeDoctorMapping", 
	           joinColumns={@JoinColumn(name="doctorid")},
	           inverseJoinColumns={@JoinColumn(name="officeid")})
	public List<Office> getOffice() {
		return office;
	}
	public void setOffice(List<Office> office) {
		this.office = office;
	}
}
