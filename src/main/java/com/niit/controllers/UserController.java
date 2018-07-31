package com.niit.controllers;


import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.dao.UserDao;
import com.niit.model.ErrorClazz;
import com.niit.model.User;

@RestController
public class UserController {
	
	public UserController(){
		System.out.println("UserController bean is created");
	}
	
	@Autowired
private UserDao userDao;
	@RequestMapping(value="/register",method=RequestMethod.POST)
	public ResponseEntity<?> registration(@RequestBody User user){
		try{
			//If Email is not unique
			if(!userDao.isEmailUnique(user.getEmail())){
				ErrorClazz errorClazz=new ErrorClazz(2,"Email id already exists.. so enter different email id");
				return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.CONFLICT);
			}
		userDao.registration(user);
        return new ResponseEntity<Void>(HttpStatus.OK);		//Only Http Status code is returned, Http Response Body is empty
        //response.status =200, response.data=""
		}catch(Exception e){//exceptions when email is null/duplicate , firstname is null, if password is null, DBConnection error
			ErrorClazz errorClazz=new ErrorClazz(1,"Unable to register user details"+e.getMessage());
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.INTERNAL_SERVER_ERROR);
			//response.status=500, response.data is errorClazz object in JSON format
			//response.data={errorCode:1,message:"Unable to register user details"}
		}
	}
	@RequestMapping(value="/login",method=RequestMethod.POST)
	//Authentication
	public ResponseEntity<?> login(@RequestBody User user,HttpSession session){
		User validUser=userDao.login(user);
		if(validUser==null){//invalid credentials
			ErrorClazz errorClazz=new ErrorClazz(5,"Email / password is incorrect..please enter valid credentials..");
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.NOT_FOUND);
		}
		else //valid credentials
		{
			//response ?
			//Session attribute - "email"
			//value of attribute - email address
			System.out.println("Logged In User email " + validUser.getEmail());
			System.out.println("Session ID " + session.getId());
			session.setAttribute("email", user.getEmail()); //Authentication 
			validUser.setOnline(true);
			userDao.updateUser(validUser);//update online_status=true where email=?
			return new ResponseEntity<User>(validUser,HttpStatus.OK);
		}
	}
	@RequestMapping(value="/logout",method=RequestMethod.GET)
	public ResponseEntity<?> logout(HttpSession session){
		//update online status of the user to false?
		//update user set online_status=false where email=?
		
		String email=(String)session.getAttribute("email");
		if(email!=null){
		System.out.println("logout User email " + email);
		System.out.println("Session ID " + session.getId());
		//update online status to false
		User user=userDao.getUser(email);
		user.setOnline(false);
		userDao.updateUser(user);
		
		//remove session attribute email and invalidate the session
		session.removeAttribute("email");
		session.invalidate();
		return new ResponseEntity<Void>(HttpStatus.OK);//success
		}
		else//session.getAttribute("email") returns null, user has not yet logged in
		{
			ErrorClazz errorClazz=new ErrorClazz(6,"please login..");
			//In frontend
			//$scope.error=response.data
			//$scope.error={errorCode:6,message:"please login.."}
			//login.html {{error.message}}
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
	}
	@RequestMapping(value="/update",method=RequestMethod.PUT)
	public ResponseEntity<?> updateUserProfile(@RequestBody User user,HttpSession session){
		//Check for Authentication
	
		String email=(String)session.getAttribute("email");
		if(email==null){//not logged in
			//what is the HttpResponse data and status code?
			ErrorClazz errorClazz=new ErrorClazz(7,"Unauthorized access..please login");
			return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);
		}
		userDao.updateUser(user);
		return new ResponseEntity<User>(user,HttpStatus.OK);
	}
}


