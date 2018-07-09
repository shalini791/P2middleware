package com.niit.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.dao.BlogPostDao;
import com.niit.dao.UserDao;
import com.niit.model.BlogPost;
import com.niit.model.ErrorClazz;
import com.niit.model.User;

@RestController
public class BlogPostController {
	public BlogPostController(){
		System.out.println("BlogPostController bean is created");
	}
	
	@Autowired
	private BlogPostDao blogPostDao;
	@Autowired
	private UserDao userDao;
	 @RequestMapping(value="/addblogpost",method=RequestMethod.POST)
	    public ResponseEntity<?> saveBlogPost(@RequestBody BlogPost blogPost,HttpSession session){
		 String email=(String)session.getAttribute("email");
	    	if(email==null){//not logged in
	    		ErrorClazz errorClazz=new ErrorClazz(7,"Unauthorized access.. please login");
	    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);//2nd callback fun
	    	}
	    	blogPost.setPostedOn(new Date());
	    	User postedBy=userDao.getUser(email);
	    	blogPost.setPostedBy(postedBy);
	    	blogPostDao.saveBlogPost(blogPost);
	    	return new ResponseEntity<Void>(HttpStatus.OK);
	 }
	
	 @RequestMapping(value="/blogsapproved",method=RequestMethod.GET)
	 public ResponseEntity<?> getBlogsApproved(HttpSession session){
		 String email=(String)session.getAttribute("email");
	    	if(email==null){//not logged in
	    		ErrorClazz errorClazz=new ErrorClazz(7,"Unauthorized access.. please login");
	    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);//2nd callback fun
	    	}
	 List<BlogPost> blogsApproved=blogPostDao.approvedblogs();
     return new ResponseEntity<List<BlogPost>>(blogsApproved,HttpStatus.OK); 
}
	 @RequestMapping(value="/blogsWaitingforapproval",method=RequestMethod.GET)
	 public ResponseEntity<?> getBlogsWaitingForApproval(HttpSession session){
		 String email=(String)session.getAttribute("email");
	    	if(email==null){//not logged in
	    		ErrorClazz errorClazz=new ErrorClazz(7,"Unauthorized access.. please login");
	    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);//2nd callback fun
	    	}
	    	User user=userDao.getUser(email);
	    	if(!user.getRole().equals("ADMIN")){
	    		ErrorClazz errorClazz=new ErrorClazz(8,"Access Denied..");
	    		return new ResponseEntity<ErrorClazz>(errorClazz,HttpStatus.UNAUTHORIZED);//2nd callback fun
	    	}
	          List<BlogPost> blogsWaitingForApproval=blogPostDao.blogsWaitingforApproval();
	          return new ResponseEntity<List<BlogPost>>(blogsWaitingForApproval,HttpStatus.OK);
	    }
}
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	
