package com.hevelian.tcm.model;

public class Contact {

	private Long id;
	private String firstname;
	private String lastname;
	private String email;
	private String tel_home;
	private String tel_mobile;
	private String streetname;
	private String housenumber;
	private String city;
	private String postcode;
	
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTel_home() {
		return tel_home;
	}
	public void setTel_home(String tel_home) {
		this.tel_home = tel_home;
	}
	public String getTel_mobile() {
		return tel_mobile;
	}
	public void setTel_mobile(String tel_mobile) {
		this.tel_mobile = tel_mobile;
	}
	public String getStreetname() {
		return streetname;
	}
	public void setStreetname(String steetname) {
		this.streetname = steetname;
	}
	public String getHousenumber() {
		return housenumber;
	}
	public void setHousenumber(String housenumber) {
		this.housenumber = housenumber;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
