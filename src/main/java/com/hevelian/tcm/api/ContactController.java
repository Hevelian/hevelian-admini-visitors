package com.hevelian.tcm.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
@RequestMapping(path = "/contact.svc")
public class ContactController {
	private static final Logger logger = LogManager.getLogger(ContactController.class);
	private final ContactService contactService;
	
	private static final String NEW_CONTACT_TEMPLATE = "panels/new_contact.html";
	
	@Autowired
	public ContactController(ContactService contactService) {
		this.contactService = contactService;
	}

	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public ResponseEntity<byte[]> deleteContact(HttpServletRequest request) throws UnsupportedEncodingException {
		Contact contact = new Contact();
		
		contact.setId(Long.parseLong(request.getParameter("contact_id")));

		contactService.deleteContact(contact);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_PLAIN);
		
		return new ResponseEntity<byte[]>("OK".getBytes("UTF-8"), responseHeaders, HttpStatus.OK);
	}
	
	@RequestMapping(value="/save", method=RequestMethod.GET)
	public ResponseEntity<byte[]> editContact(HttpServletRequest request) throws UnsupportedEncodingException {
		Contact contact = new Contact();
		
		contact.setId(Long.parseLong(request.getParameter("contact_id")));
		contact.setCity(request.getParameter("city"));
		contact.setEmail(request.getParameter("email"));
		contact.setFirstname(request.getParameter("firstname"));
		contact.setHousenumber(request.getParameter("housenumber"));
		contact.setLastname(request.getParameter("lastname"));
		contact.setPostcode(request.getParameter("postcode"));
		contact.setStreetname(request.getParameter("streetname"));
		contact.setTel_home(request.getParameter("tel_home"));
		contact.setTel_mobile(request.getParameter("tel_mobile"));
		
		contactService.saveContact(contact);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_PLAIN);
		
		return new ResponseEntity<byte[]>("OK".getBytes("UTF-8"), responseHeaders, HttpStatus.OK);
		
	}

	@RequestMapping(value="/new", method=RequestMethod.GET)
	public ResponseEntity<byte[]> newContact(HttpServletRequest request) throws UnsupportedEncodingException {
		Contact contact = new Contact();
		
		contact.setCity(request.getParameter("city"));
		contact.setEmail(request.getParameter("email"));
		contact.setFirstname(request.getParameter("firstname"));
		contact.setHousenumber(request.getParameter("housenumber"));
		contact.setLastname(request.getParameter("lastname"));
		contact.setPostcode(request.getParameter("postcode"));
		contact.setStreetname(request.getParameter("streetname"));
		contact.setTel_home(request.getParameter("tel_home"));
		contact.setTel_mobile(request.getParameter("tel_mobile"));
		
		contactService.newContact(contact);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_PLAIN);
		
		return new ResponseEntity<byte[]>("OK".getBytes("UTF-8"), responseHeaders, HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/all", method=RequestMethod.GET)
	public ResponseEntity<byte[]> getAllContacts(HttpServletRequest request) throws UnsupportedEncodingException {
		logger.debug("ContactController: get all contacts");
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		String contacts = contactService.getAllContacts();
		return new ResponseEntity<byte[]>(contacts.getBytes("UTF-8"), responseHeaders, HttpStatus.OK);
	}
		
	@RequestMapping(value="/template/new", method=RequestMethod.GET)
	public ResponseEntity<byte[]> getNewContactTemplate(HttpServletRequest request) throws IOException {

		InputStream input = getResourceAsStream(request, NEW_CONTACT_TEMPLATE);
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		for(String line; (line = br.readLine())!=null;) {
			sb.append(line);
		}
		br.close();
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_HTML);
		
		return new ResponseEntity<byte[]>(sb.toString().getBytes("UTF-8"), responseHeaders, HttpStatus.OK);
	}
	
	/**
	 * Helper function to find a template file as a resource and return the input stream to it.
	 * @param request
	 * @param templateName
	 * @return
	 */
	private InputStream getResourceAsStream(HttpServletRequest request, String templateName) {
		InputStream input = request.getServletContext().getResourceAsStream(templateName);
		return input;
	}
    
}
