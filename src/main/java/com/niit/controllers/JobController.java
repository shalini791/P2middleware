package com.niit.controllers;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.niit.dao.JobDao;
import com.niit.dao.UserDao;
import com.niit.model.ErrorClazz;
import com.niit.model.Job;
import com.niit.model.User;
@RestController
public class JobController {
	public JobController(){
		System.out.println("JobController bean is created");
	}
	@Autowired
	private JobDao jobDao;
	private UserDao userDao;
	@RequestMapping(value="/addjob",method=RequestMethod.POST)
	public ResponseEntity<?> saveJob(@RequestBody Job job,HttpSession session){
		String email=(String)session.getAttribute("email");
		if(email==null) {
		ErrorClazz errorClazz=new ErrorClazz(7,"Unautherized Access..please login");
		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
		//to check if the loggedin user is admin or not
		User user=userDao.getUser(email);
		if(!user.getRole().equals("ADMIN")){
			ErrorClazz errorClazz=new ErrorClazz(8,"Acces Denied..");
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);	
		}
		try {
		job.setPostedON(new Date());
		job.setActive(true);
		jobDao.saveJob(job);
		return new ResponseEntity<Void>(HttpStatus.OK);	
		}catch (Exception e) {
			ErrorClazz errorClazz=new ErrorClazz(4,"Unable to insert job details.."+e.getMessage());
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.INTERNAL_SERVER_ERROR);		
		}
		
	}
}
