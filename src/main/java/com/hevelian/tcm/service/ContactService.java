package com.hevelian.tcm.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.hevelian.tcm.database.ContactStore;
import com.hevelian.tcm.model.Contact;

@Service
public class ContactService {

	public void newContact(Contact contact) {
		ContactStore store = new ContactStore();
		
		String city = contact.getCity();
		String email = contact.getEmail();
		String firstname = contact.getFirstname();
		String housenumber = contact.getHousenumber();
		String lastname = contact.getLastname();
		String postcode = contact.getPostcode();
		String streetname = contact.getStreetname();
		String tel_home = contact.getTel_home();
		String tel_mobile = contact.getTel_mobile();
		
		store.NewContact(city, email, firstname, housenumber, lastname, postcode, streetname, tel_home, tel_mobile);
	}
	
	public void saveContact(Contact contact) {
		ContactStore store = new ContactStore();
		
		Long id = contact.getId();
		String city = contact.getCity();
		String email = contact.getEmail();
		String firstname = contact.getFirstname();
		String housenumber = contact.getHousenumber();
		String lastname = contact.getLastname();
		String postcode = contact.getPostcode();
		String streetname = contact.getStreetname();
		String tel_home = contact.getTel_home();
		String tel_mobile = contact.getTel_mobile();
		
		store.SaveContact(id, city, email, firstname, housenumber, lastname, postcode, streetname, tel_home, tel_mobile);
	}

	public void deleteContact(Contact contact) {
		ContactStore store = new ContactStore();
		
		store.DeleteContact(contact.getId());
	}
	
	public String getAllContacts() {
		ContactStore store = new ContactStore();
		ArrayList<Contact> contacts = store.GetAllContacts();
		
		return new Gson().toJson(contacts);
	}

}
