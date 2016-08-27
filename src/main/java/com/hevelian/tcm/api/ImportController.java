package com.hevelian.tcm.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hevelian.tcm.model.Contact;
import com.hevelian.tcm.service.ContactService;

@Controller
@RequestMapping(path = "/import.svc")
public class ImportController {
	private final ContactService contactService;

	String contactsFilename = "/Users/cb/Desktop/cleaned-customers-cb-001-tab.txt";
	
	@Autowired
	public ImportController(ContactService contactService) {
		this.contactService = contactService;
	}

	@RequestMapping(value="/contacts", method=RequestMethod.GET)
	public ResponseEntity<byte[]> importContacts(HttpServletRequest request) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(contactsFilename))));
		for(String line; (line = br.readLine())!=null;) {
			
			String[] parts = line.split("	");
			
			System.out.println("DATA:" + line);
			if(parts.length<13) continue;
			
			Contact contact = new Contact();

			if(parts.length>13) {
				contact.setCity(parts[12]);
			}
			contact.setEmail(parts[6]);
			contact.setFirstname(parts[2]);
			contact.setHousenumber(parts[11]);
			contact.setLastname(parts[3]);
			if(parts.length>13) {
				contact.setPostcode(parts[13]);
			}
			contact.setStreetname(parts[9]);
			contact.setTel_home(parts[7]);
			contact.setTel_mobile(parts[8]);
			
			contactService.newContact(contact);
		}
		br.close();
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_PLAIN);
		
		return new ResponseEntity<byte[]>("OK".getBytes("UTF-8"), responseHeaders, HttpStatus.OK);

	}

}
