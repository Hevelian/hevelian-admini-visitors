package com.hevelian.tcm.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hevelian.tcm.database.VisitStore;
import com.hevelian.tcm.model.Visit;
import com.hevelian.tcm.model.VisitContact;

@Service
public class VisitService {

	public void newVisit(Visit visit) throws SQLException, ParseException {
		VisitStore store = new VisitStore();
		
		Long id_contact = visit.getId_contact();
		int amount = visit.getAmount();
		int paid = visit.getPaid();
		Date when = visit.getWhen();
		
		store.newVisit(id_contact, paid, amount, when);
	}
	
	public void deleteVisit(Long id) {
		VisitStore store = new VisitStore();
		store.deleteVisit(id);
	}
	
	public void saveVisit(Visit visit) throws SQLException, ParseException {
		VisitStore store = new VisitStore();
		
		Long id = visit.getId();
		Long id_contact = visit.getId_contact();
		int amount = visit.getAmount();
		int paid = visit.getPaid();
		Date when = visit.getWhen();
		
		store.saveVisit(id, id_contact, paid, amount, when);
	}

	public String getVisitYears() {
		VisitStore store = new VisitStore();
		return new Gson().toJson(store.getVisitYears());
	}
	
	public String getInvoice(String number) {
		VisitStore store = new VisitStore();
		return new Gson().toJson(store.getInvoice(number));
	}
	
	public void deleteContact(String contact_id) {
		VisitStore store = new VisitStore();
		store.deleteContact(Long.parseLong(contact_id));
	}
	
	public String getAllVisits(String filterYear, String filterDetail) {
		VisitStore store = new VisitStore();
		ArrayList<VisitContact> visitcontacts = store.getVisits(filterYear, filterDetail);
		
		System.out.println("ALL VISITCONTACTS COUNT: " + visitcontacts.size());
		
		Gson gson = new GsonBuilder()
				   .setDateFormat("dd MMMM yyyy").create();
		
		return gson.toJson(visitcontacts);
	}
}
