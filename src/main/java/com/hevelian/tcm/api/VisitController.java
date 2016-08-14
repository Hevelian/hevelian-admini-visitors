package com.hevelian.tcm.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hevelian.tcm.model.Visit;
import com.hevelian.tcm.service.VisitService;

@Controller
@RequestMapping(path = "/visit.svc")
public class VisitController {
	private static final Logger logger = LogManager.getLogger(VisitController.class);
	private final VisitService visitService;
	
	private static final String NEW_VISIT_TEMPLATE = "panels/new_visit.html";
	
	@Autowired
	public VisitController(VisitService visitService) {
		this.visitService = visitService;
	}
	
	@RequestMapping(value="/years", method=RequestMethod.GET)
	public ResponseEntity<byte[]> getVisitYears(HttpServletRequest request) throws UnsupportedEncodingException, ParseException, SQLException {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		return new ResponseEntity<byte[]>(visitService.getVisitYears().getBytes("UTF-8"), responseHeaders, HttpStatus.OK);
	}
	
	@RequestMapping(value="/invoice/{invoiceNumber}", method=RequestMethod.GET)
	public ResponseEntity<byte[]> getVisit(HttpServletRequest request, @PathVariable String invoiceNumber) throws UnsupportedEncodingException, ParseException, SQLException {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		return new ResponseEntity<byte[]>(visitService.getInvoice(invoiceNumber).getBytes("UTF-8"), responseHeaders, HttpStatus.OK);
	}
	
	@RequestMapping(value="/delete/contact", method=RequestMethod.GET)
	public ResponseEntity<byte[]> deleteContact(HttpServletRequest request) throws UnsupportedEncodingException, ParseException, SQLException {
		
		visitService.deleteContact(request.getParameter("contact_id"));
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_PLAIN);
		
		return new ResponseEntity<byte[]>("OK".getBytes("UTF-8"), responseHeaders, HttpStatus.OK);
	}

	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public ResponseEntity<byte[]> deleteVisit(HttpServletRequest request) throws UnsupportedEncodingException, ParseException, SQLException {
		
		visitService.deleteVisit(Long.parseLong(request.getParameter("id")));
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_PLAIN);
		
		return new ResponseEntity<byte[]>("OK".getBytes("UTF-8"), responseHeaders, HttpStatus.OK);
	}

	@RequestMapping(value="/new", method=RequestMethod.GET)
	public ResponseEntity<byte[]> newVisit(HttpServletRequest request) throws UnsupportedEncodingException, ParseException, SQLException {
		Visit visit = new Visit();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
		Date when = sdf.parse(request.getParameter("when"));

		visit.setId_contact(Long.parseLong(request.getParameter("id_contact")));
		visit.setAmount(Integer.parseInt(request.getParameter("amount")));
		visit.setWhen(when);
		visit.setPaid((request.getParameter("paid").equalsIgnoreCase("no"))? 0 : 1);
		
		visitService.newVisit(visit);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_PLAIN);
		
		return new ResponseEntity<byte[]>("OK".getBytes("UTF-8"), responseHeaders, HttpStatus.OK);
	}
	
	@RequestMapping(value="/save", method=RequestMethod.GET)
	public ResponseEntity<byte[]> saveVisit(HttpServletRequest request) throws UnsupportedEncodingException, ParseException, SQLException {
		Visit visit = new Visit();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
		Date when = sdf.parse(request.getParameter("when"));

		visit.setId(Long.parseLong(request.getParameter("id")));
		visit.setId_contact(Long.parseLong(request.getParameter("id_contact")));
		visit.setAmount(Integer.parseInt(request.getParameter("amount")));
		visit.setWhen(when);
		visit.setPaid((request.getParameter("paid").equalsIgnoreCase("no"))? 0 : 1);
		
		visitService.saveVisit(visit);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_PLAIN);
		
		return new ResponseEntity<byte[]>("OK".getBytes("UTF-8"), responseHeaders, HttpStatus.OK);
	}

	@RequestMapping(value="/all/{filterYear}/{filterDetail}", method=RequestMethod.GET)
	public ResponseEntity<byte[]> getAllVisits(HttpServletRequest request, @PathVariable String filterYear, @PathVariable String filterDetail) throws UnsupportedEncodingException {
		logger.debug("VisitController: get all visits");
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		String visits = visitService.getAllVisits(filterYear, filterDetail);
		return new ResponseEntity<byte[]>(visits.getBytes("UTF-8"), responseHeaders, HttpStatus.OK);
	}

	@RequestMapping(value="/template/new", method=RequestMethod.GET)
	public ResponseEntity<byte[]> getNewContactTemplate(HttpServletRequest request) throws IOException {

		InputStream input = getResourceAsStream(request, NEW_VISIT_TEMPLATE);
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
