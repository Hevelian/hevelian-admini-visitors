package com.hevelian.tcm.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.hevelian.tcm.model.Contact;

public class ContactStore {
	private static final String SQL_CREATE_CONTACTS_TABLE	= "create table if not exists tcm_contacts(id bigint identity, firstname varchar(32), lastname varchar(32), email varchar(32), tel_home varchar(16), tel_mobile varchar(16), streetname varchar(64), housenumber varchar(16), city varchar(64), postcode varchar(16))";
	private static final String SQL_SELECT_ALL_CONTACTS		= "select * from tcm_contacts";
	private static final String SQL_INSERT_NEW_CONTACT		= "insert into tcm_contacts(id,city,email,firstname,housenumber,lastname,postcode,streetname,tel_home,tel_mobile) VALUES (?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE_CONTACT			= "update tcm_contacts set (city,email,firstname,housenumber,lastname,postcode,streetname,tel_home,tel_mobile) = (?,?,?,?,?,?,?,?,?) where id=?";
	private static final String SQL_DELETE_CONTACT			= "delete from tcm_contacts where id=?";
	
	private static Configuration config						= new Configuration();

	public ContactStore() {
		CheckTableExists();
	}
	
	public void DeleteContact(Long id) {
		String jdbc_url = config.getProperty(Configuration.TAG_DATABASE);
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbc_url, "sa", "");

			ps = conn.prepareStatement(SQL_DELETE_CONTACT);
			ps.setLong(1, id);
			
			ps.execute();
			ps.close();
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	public void SaveContact(Long id, String city, String email, String firstname, String housenumber, String lastname, String postcode, String streetname, String tel_home, String tel_mobile) {
		String jdbc_url = config.getProperty(Configuration.TAG_DATABASE);
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbc_url, "sa", "");

			ps = conn.prepareStatement(SQL_UPDATE_CONTACT);
			ps.setString(1, city);
			ps.setString(2, email);
			ps.setString(3, firstname);
			ps.setString(4, housenumber);
			ps.setString(5, lastname);
			ps.setString(6, postcode);
			ps.setString(7, streetname);
			ps.setString(8, tel_home);
			ps.setString(9, tel_mobile);
			ps.setLong(10, id);
			
			ps.execute();
			ps.close();
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public void NewContact(String city, String email, String firstname, String housenumber, String lastname, String postcode, String streetname, String tel_home, String tel_mobile) {
		String jdbc_url = config.getProperty(Configuration.TAG_DATABASE);
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbc_url, "sa", "");

			ps = conn.prepareStatement(SQL_INSERT_NEW_CONTACT);
			ps.setLong(1, NextContactId());					// not very efficient, but we will have at most a couple of hundred records
			ps.setString(2, city);
			ps.setString(3, email);
			ps.setString(4, firstname);
			ps.setString(5, housenumber);
			ps.setString(6, lastname);
			ps.setString(7, postcode);
			ps.setString(8, streetname);
			ps.setString(9, tel_home);
			ps.setString(10, tel_mobile);
			
			ps.execute();
			ps.close();
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	public Long NextContactId() {
		ArrayList<Contact> contacts = GetAllContacts();
		
		Long id = 0L;
		for(Contact contact : contacts) {
			if(contact.getId() > id) {
				id = contact.getId();
			}
		}
		
		return id + 1;
	}
	
	public ArrayList<Contact> GetAllContacts() {
		String jdbc_url = config.getProperty(Configuration.TAG_DATABASE);
		Connection conn = null;
		Statement sCreate = null;
		ResultSet rs = null;

		ArrayList<Contact> contacts = new ArrayList<Contact>();
		
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbc_url, "sa", "");
			sCreate = conn.createStatement();
			rs = sCreate.executeQuery(SQL_SELECT_ALL_CONTACTS);

			while(rs.next()) {
				Contact contact = new Contact();
				contact.setId(rs.getLong("id"));
				contact.setCity(rs.getString("city"));
				contact.setEmail(rs.getString("email"));
				contact.setFirstname(rs.getString("firstname"));
				contact.setHousenumber(rs.getString("housenumber"));
				contact.setLastname(rs.getString("lastname"));
				contact.setPostcode(rs.getString("postcode"));
				contact.setStreetname(rs.getString("streetname"));
				contact.setTel_home(rs.getString("tel_home"));
				contact.setTel_mobile(rs.getString("tel_mobile"));
				contacts.add(contact);
				
			}
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
			return contacts;
		}
		return contacts;
	}
	
	private void CheckTableExists() {
		String jdbc_url = config.getProperty(Configuration.TAG_DATABASE);
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbc_url, "sa", "");

			ps = conn.prepareStatement(SQL_CREATE_CONTACTS_TABLE);
			ps.execute();
			ps.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
