package com.patienttouch.hibernate;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;



@Entity
@Table(name="Office")
public class Office {
	private int officeid;
	private String name;
	private String streetAddress1;
	private String streetAddress2;
	private String city;
	private String cityShort;
	private String state;
	private String zip;
	private String phone;
	private boolean mainOffice;
	private Practice practice;
	private List<Doctor> doctor;
	
	@Id
	@GeneratedValue
	public int getOfficeid() {
		return officeid;
	}
	public void setOfficeid(int officeid) {
		this.officeid = officeid;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStreetAddress1() {
		return streetAddress1;
	}
	public void setStreetAddress1(String streetAddress1) {
		this.streetAddress1 = streetAddress1;
	}
	public String getStreetAddress2() {
		return streetAddress2;
	}
	public void setStreetAddress2(String streetAddress2) {
		this.streetAddress2 = streetAddress2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCityShort() {
		return cityShort;
	}
	public void setCityShort(String cityShort) {
		this.cityShort = cityShort;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public boolean isMainOffice() {
		return mainOffice;
	}
	public void setMainOffice(boolean mainOffice) {
		this.mainOffice = mainOffice;
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
	           joinColumns={@JoinColumn(name="officeid")},
	           inverseJoinColumns={@JoinColumn(name="doctorid")})
	public List<Doctor> getDoctor() {
		return doctor;
	}
	public void setDoctor(List<Doctor> doctor) {
		this.doctor = doctor;
	}
}
