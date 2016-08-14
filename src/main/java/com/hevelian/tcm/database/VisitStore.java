package com.hevelian.tcm.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.hevelian.tcm.model.Contact;
import com.hevelian.tcm.model.Visit;
import com.hevelian.tcm.model.VisitContact;

public class VisitStore {
	private static final String SQL_CREATE_VISIT_TABLE	= "create table if not exists tcm_visits(id bigint identity, id_contact bigint, invoice varchar(16), paid int, amount int, when date)";
	private static final String SQL_INSERT_NEW_VISIT	= "insert into tcm_visits(id, id_contact, invoice, paid, amount, when) VALUES (?,?,?,?,?,?)";
//	private static final String SQL_SELECT_INVOICE_N	= "select count(invoice) as cnt from tcm_visits where left(invoice,4) = ?";
	private static final String SQL_SELECT_INVOICE_MAX	= "select max(invoice) as invoice_max from tcm_visits where left(invoice,4) = ?";
	private static final String SQL_SELECT_YEARS		= "select distinct year(when) as year from tcm_visits order by year(when) desc";
	private static final String SQL_DELETE_CONTACT		= "delete from tcm_visits where id_contact = ?";
	private static final String SQL_DELETE_VISIT		= "delete from tcm_visits where id =?";
	private static final String SQL_UPDATE_VISIT		= "update tcm_visits set (id_contact,paid,amount,when) = (?,?,?,?) where id = ?";
	
	private static final String SQL_SELECT_VISITS		= "select tcm_visits.*, tcm_contacts.firstname, tcm_contacts.lastname, tcm_contacts.email, tcm_contacts.tel_home, tcm_contacts.tel_mobile,"
															+ " tcm_contacts.streetname, tcm_contacts.housenumber, tcm_contacts.postcode, tcm_contacts.city"
															+ "	from tcm_visits left join tcm_contacts on tcm_visits.id_contact = tcm_contacts.id";

	private static final String SQL_SELECT_ORDERBY		= " order by when desc";
	
	private static final String SQL_SELECT_Q1			= " quarter(when) = 1";
	private static final String SQL_SELECT_Q2			= " quarter(when) = 2";
	private static final String SQL_SELECT_Q3			= " quarter(when) = 3";
	private static final String SQL_SELECT_Q4			= " quarter(when) = 4";
	
	private static final String SQL_SELECT_H1			= " (quarter(when) = 1 or quarter(when) = 2)";
	private static final String SQL_SELECT_H2			= " (quarter(when) = 3 or quarter(when) = 4)";
	
	private static final String SQL_SELECT_THIS_Q		= " quarter(when) = quarter(current_date())";
	private static final String SQL_SELECT_THIS_M		= " month(when) = month(current_date())";
	private static final String SQL_SELECT_THIS_W		= " week(when) = week(current_date())";
	
	private static final String SQL_SELECT_INVOICE		= " where tcm_visits.id = ?";
	
	private static Configuration config					= new Configuration();
	
	public VisitStore() {
		CheckTableExists();
	}
	
	public VisitContact getInvoice(String number) {
		String jdbc_url = config.getProperty(Configuration.TAG_DATABASE);
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		VisitContact vc = new VisitContact();
		
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbc_url, "sa", "");
			ps = conn.prepareStatement(SQL_SELECT_VISITS + SQL_SELECT_INVOICE);
			ps.setLong(1, Long.parseLong(number));

			rs = ps.executeQuery();
			rs.next();
			
			Contact contact = new Contact();
			contact.setId(rs.getLong("id_contact"));
			contact.setEmail(rs.getString("email"));
			contact.setFirstname(rs.getString("firstname"));
			contact.setLastname(rs.getString("lastname"));
			contact.setTel_home(rs.getString("tel_home"));
			contact.setTel_mobile(rs.getString("tel_mobile"));
			contact.setCity(rs.getString("city"));
			contact.setPostcode(rs.getString("postcode"));
			contact.setHousenumber(rs.getString("housenumber"));
			contact.setStreetname(rs.getString("streetname"));
			vc.setContact(contact);

			Visit visit = new Visit();
			visit.setId(rs.getLong("id"));
			visit.setAmount(rs.getInt("amount"));
			visit.setInvoice(rs.getString("invoice"));
			visit.setPaid(rs.getInt("paid"));
			visit.setWhen(rs.getDate("when"));
			vc.setVisit(visit);

			ps.close();
			conn.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return vc;
	}
	
	public void deleteVisit(Long id) {
		String jdbc_url = config.getProperty(Configuration.TAG_DATABASE);
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbc_url, "sa", "");
			ps = conn.prepareStatement(SQL_DELETE_VISIT);
			ps.setLong(1, id);

			ps.execute();

			ps.close();
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteContact(Long id) {
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
	}
	
	public ArrayList<String> getVisitYears() {
		String jdbc_url = config.getProperty(Configuration.TAG_DATABASE);
		Connection conn = null;
		Statement sCreate = null;
		ResultSet rs = null;

		ArrayList<String> years = new ArrayList<String>();
		
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbc_url, "sa", "");
			sCreate = conn.createStatement();
			rs = sCreate.executeQuery(SQL_SELECT_YEARS);

			while(rs.next()) {
				years.add(rs.getString("year"));
			}
			conn.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return years;
	}
	
	public ArrayList<VisitContact> getVisits(String filterYear, String filterDetail) {
		String jdbc_url = config.getProperty(Configuration.TAG_DATABASE);
		Connection conn = null;
		Statement sCreate = null;
		ResultSet rs = null;

		ArrayList<VisitContact> visitcontacts = new ArrayList<VisitContact>();
		
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbc_url, "sa", "");
			sCreate = conn.createStatement();
			rs = sCreate.executeQuery(SQL_SELECT_VISITS + buildFilterClause(filterYear, filterDetail) + SQL_SELECT_ORDERBY);

			while(rs.next()) {
				
				VisitContact visitcontact = new VisitContact();
				
				Contact contact = new Contact();
				contact.setId(rs.getLong("id_contact"));
				contact.setEmail(rs.getString("email"));
				contact.setFirstname(rs.getString("firstname"));
				contact.setLastname(rs.getString("lastname"));
				contact.setTel_home(rs.getString("tel_home"));
				contact.setTel_mobile(rs.getString("tel_mobile"));
				visitcontact.setContact(contact);

				Visit visit = new Visit();
				visit.setId(rs.getLong("id"));
				visit.setAmount(rs.getInt("amount"));
				visit.setInvoice(rs.getString("invoice"));
				visit.setPaid(rs.getInt("paid"));
				visit.setWhen(rs.getDate("when"));
				visitcontact.setVisit(visit);
				
				visitcontacts.add(visitcontact);
				
			}
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
			return visitcontacts;
		}
		return visitcontacts;
	}
	
	private String buildFilterClause(String filterYear, String filterDetail) {
		String filter = "";
		
		if(!filterYear.equalsIgnoreCase("all_years")) {
			filter += " where when between ";
			filter += "'" + filterYear + "-01-01' and ";
			filter += "'" + filterYear + "-12-31'";
		}

		switch(filterDetail) {
		case "q1":
			filter += ((filter.length()!=0)? " and": " where ") + SQL_SELECT_Q1;
			break;
		case "q2":
			filter += ((filter.length()!=0)? " and": " where ") + SQL_SELECT_Q2;
			break;
		case "q3":
			filter += ((filter.length()!=0)? " and": " where ") + SQL_SELECT_Q3;
			break;
		case "q4":
			filter += ((filter.length()!=0)? " and": " where ") + SQL_SELECT_Q4;
			break;
		case "h1":
			filter += ((filter.length()!=0)? " and": " where ") + SQL_SELECT_H1;
			break;
		case "h2":
			filter += ((filter.length()!=0)? " and": " where ") + SQL_SELECT_H2;
			break;
		case "this_quarter":
			filter += ((filter.length()!=0)? " and": " where ") + SQL_SELECT_THIS_Q;
			break;
		case "this_month":
			filter += ((filter.length()!=0)? " and": " where ") + SQL_SELECT_THIS_M;
			break;
		case "this_week":
			filter += ((filter.length()!=0)? " and": " where ") + SQL_SELECT_THIS_W;
			break;
					
		}
		
		return filter;
	}
	
	@SuppressWarnings("deprecation")
	public void newVisit(Long id_contact, int paid, int amount, Date when) throws SQLException, ParseException {
		String jdbc_url = config.getProperty(Configuration.TAG_DATABASE);
		Connection conn = null;
		PreparedStatement ps = null;
		
		Long invoiceCount = newInvoiceNumberForYear(when.getYear()+1900);
		String invoice = String.format("%08d", invoiceCount);
		java.sql.Date d = new java.sql.Date(when.getTime());

		System.out.println("PARSED DATE:" + (when.getYear()+1900) + " " + (when.getMonth()+1) + ", INVOICE: " + invoice);
		
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbc_url, "sa", "");

			ps = conn.prepareStatement(SQL_INSERT_NEW_VISIT);
			ps.setLong(1, Long.parseLong(invoice));
			ps.setLong(2, id_contact);
			ps.setString(3, invoice);
			ps.setInt(4, paid);
			ps.setInt(5, amount);
			ps.setDate(6, d);
			
			ps.execute();
			ps.close();
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public void saveVisit(Long id, Long id_contact, int paid, int amount, Date when) throws SQLException, ParseException {
		String jdbc_url = config.getProperty(Configuration.TAG_DATABASE);
		Connection conn = null;
		PreparedStatement ps = null;
		
		java.sql.Date d = new java.sql.Date(when.getTime());

		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbc_url, "sa", "");

			ps = conn.prepareStatement(SQL_UPDATE_VISIT);
			ps.setLong(1, id_contact);
			ps.setInt(2, paid);
			ps.setInt(3, amount);
			ps.setDate(4, d);
			ps.setLong(5, id);
			
			ps.execute();
			ps.close();
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	/*
	private Long countInvoicesForThis(int year) {
		String jdbc_url = config.getProperty(Configuration.TAG_DATABASE);
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Long invoiceCount = 0L;
		
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbc_url, "sa", "");

			ps = conn.prepareStatement(SQL_SELECT_INVOICE_N);
			ps.setString(1, String.format("%04d", year));
			
			rs = ps.executeQuery();
			rs.next();
			invoiceCount = rs.getLong("cnt");
			
			ps.close();
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return invoiceCount;
	}
*/	
	private Long newInvoiceNumberForYear(int year) {
		String jdbc_url = config.getProperty(Configuration.TAG_DATABASE);
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Long invoiceNumber = year * 10000L;
		
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbc_url, "sa", "");

			ps = conn.prepareStatement(SQL_SELECT_INVOICE_MAX);
			ps.setString(1, String.format("%04d", year));
			
			rs = ps.executeQuery();
			rs.next();
			invoiceNumber = rs.getLong("invoice_max");
			
			ps.close();
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(invoiceNumber==0) {
			invoiceNumber = year * 10000L;
		}
		return invoiceNumber + 1;
	}

	private void CheckTableExists() {
		String jdbc_url = config.getProperty(Configuration.TAG_DATABASE);
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbc_url, "sa", "");

			ps = conn.prepareStatement(SQL_CREATE_VISIT_TABLE);
			ps.execute();
			ps.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
