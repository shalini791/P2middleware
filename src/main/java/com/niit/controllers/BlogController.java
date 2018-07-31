package com.niit.controllers;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.dao.BlogCommentsDao;
import com.niit.dao.BlogDao;
import com.niit.dao.BlogPostLikesDao;
import com.niit.dao.UserDao;
import com.niit.model.BlogComments;
import com.niit.model.BlogPostLikes;
import com.niit.model.Blogs;
import com.niit.model.ErrorClazz;
import com.niit.model.User;

@RestController
public class BlogController {
     @Autowired
	private BlogDao blogdao;
     @Autowired
 	private BlogCommentsDao blogCommentsDao;
     @Autowired
  	private BlogPostLikesDao blogPostLikesDao;
    @Autowired
    private UserDao userDao;
	
     public BlogController()
	 {
		System.out.println("BlogController class  Instantiated");
	 }
     
     @RequestMapping(value="/Postblog",method=RequestMethod.POST)
     public ResponseEntity<?> PostBlog(@RequestBody Blogs blog,HttpSession ssn)
     {
    	System.out.println("In BlogController PostBlog function Invoked");
         if(ssn.getAttribute("email")==null)
    	{
    		ErrorClazz ec=new ErrorClazz(30,"Please Login");
    		return new ResponseEntity<ErrorClazz>(ec,HttpStatus.UNAUTHORIZED);
    	}
    	String email=(String)ssn.getAttribute("email");
    	try{
    	User userpostedblog=userDao.getUser(email);
    	blog.setPostedBy(userpostedblog);
    	blog.setPostedOn(new Date());
    	blog.setApproved(false);
    	blog.setLikes(0);
    	blogdao.PostBlog(blog);
    	return new ResponseEntity<Void>(HttpStatus.OK);
    	}
    	catch(Exception e)
    	{
    		ErrorClazz ec=new ErrorClazz(8,"Unable to post blog due to some error");
    		return new ResponseEntity<ErrorClazz>(ec,HttpStatus.INTERNAL_SERVER_ERROR); 
    	}
     }
     
  
      
     @RequestMapping(value="/Getapprovedblogs",method=RequestMethod.GET)
     public ResponseEntity<?> GetApprovedBlogsList(HttpSession session)
     {   
    	System.out.println("In BlogController GetApprovedBlogslist function Invoked");
    	 if(session.getAttribute("email")==null)
     	{
     		ErrorClazz ec=new ErrorClazz(3,"Please Login");
     		return new ResponseEntity<ErrorClazz>(ec,HttpStatus.UNAUTHORIZED);
     	} 
    	 try{
    	 List<Blogs>Approved_Blogs=blogdao.GetApprovedBlogs();
    	 return new ResponseEntity<List<Blogs>>(Approved_Blogs,HttpStatus.OK);
    	 }
    	 catch(Exception e)
    	 {
    		 ErrorClazz ec=new ErrorClazz(19,"Unable to get blogs due to some error");
     		return new ResponseEntity<ErrorClazz>(ec,HttpStatus.INTERNAL_SERVER_ERROR);
    	 }
     }
     
     
     @RequestMapping(value="/Getblogswaitingapproval",method=RequestMethod.GET)
     public ResponseEntity<?>GetBlogsWaitingApprovalList(HttpSession session)
     {   
    	 System.out.println("In BlogController GetBlogsWaitingApproval function Invoked");
    	 if(session.getAttribute("email")==null)
      	{
      		ErrorClazz ec=new ErrorClazz(18,"Please Login");
      		return new ResponseEntity<ErrorClazz>(ec,HttpStatus.UNAUTHORIZED);
      	} 
    	 String email=(String)session.getAttribute("email");
    	 try{
 		  User user=userDao.getUser(email);
 		  if(user.getRole().equals("ADMIN"))
 		  {
 			List<Blogs>Blogs_waiting_approval=blogdao.GetBlogsWaitingApproval();  
 			return new ResponseEntity<List<Blogs>>(Blogs_waiting_approval,HttpStatus.OK);
 		  }
 		  else
 		  {
 			 ErrorClazz ec=new ErrorClazz(1,"Access Denied");
       		return new ResponseEntity<ErrorClazz>(HttpStatus.UNAUTHORIZED);  
 		  } 
    	 }
    	 catch(Exception e)
    	 {
    		ErrorClazz ec=new ErrorClazz(20,"Unable to get blogs due to some error");
     		return new ResponseEntity<ErrorClazz>(ec,HttpStatus.INTERNAL_SERVER_ERROR);
    	 }
    	 
     }
     
     
     @RequestMapping(value="/Approveblog/{id}",method=RequestMethod.GET)
     public ResponseEntity<?> UpdateBlog(@PathVariable int id,HttpSession session)
     {
    	System.out.println("In BlogController UpdateBlog function is Invoked"); 
    	if(session.getAttribute("email")==null)
       	{
       		ErrorClazz ec=new ErrorClazz(75,"Please Login");
       		return new ResponseEntity<ErrorClazz>(ec,HttpStatus.UNAUTHORIZED);
       	} 
    	String email=(String)session.getAttribute("email");
    	try
    	{
    		User user=userDao.getUser(email);
    		if(user.getRole().equals("ADMIN"))
    		{   
    			Blogs blog=blogdao.GetBlog(id);
    			blog.setApproved(true);
    			blogdao.UpdateBlogPost(blog);
    			return new ResponseEntity<Void>(HttpStatus.OK);
    		}
    		else
    		{
    			ErrorClazz ec=new ErrorClazz(75,"Access Denied");
           		return new ResponseEntity<ErrorClazz>(ec,HttpStatus.UNAUTHORIZED);	
    		}
    	}
    	catch(Exception e)
    	{
    		ErrorClazz ec=new ErrorClazz(75,"Could not update blog due to some error");
       		return new ResponseEntity<ErrorClazz>(ec,HttpStatus.INTERNAL_SERVER_ERROR);		
    	}
     }
     
     @RequestMapping(value="/Deleteblog/{id}",method=RequestMethod.GET)
     public ResponseEntity<?> deleteBlog(@PathVariable int id,HttpSession session)
     {
    	System.out.println("In BlogController deleteBlog function is Invoked"); 
    	if(session.getAttribute("email")==null)
       	{
       		ErrorClazz ec=new ErrorClazz(75,"Please Login");
       		return new ResponseEntity<ErrorClazz>(ec,HttpStatus.UNAUTHORIZED);
       	} 
    	
    			Blogs blog=blogdao.GetBlog(id);
    			List<BlogComments> blogComments=blogCommentsDao.GetBlogComments(id);
    			
    			for(BlogComments blog1:blogComments) {
    				blogCommentsDao.DeleteBlogComments(blog1);
    			}
    			/*List<BlogPostLikes> blogPostLikes=blogPostLikesDao.GetAllLikes(id);
    			
    			for(BlogPostLikes blog2:blogPostLikes) {
    				blogPostLikesDao.DeleteBlogLikes(blog2);
    			}*/
    			blogPostLikesDao.DeleteBlogLikes(id);
    			blogdao.DeleteBlogPost(blog);
    			return new ResponseEntity<Void>(HttpStatus.OK);
    		
    	}
    	
     
     
     
     @RequestMapping(value="Getblog/{id}",method=RequestMethod.GET)
     public ResponseEntity<?> GetBlog(@PathVariable int id,HttpSession session)
     {   
    	 System.out.println("In BlogController GetBlogUsingId function Invoked");
    	 if(session.getAttribute("email")==null)
       	{
       		ErrorClazz ec=new ErrorClazz(75,"Please Login");
       		return new ResponseEntity<ErrorClazz>(ec,HttpStatus.UNAUTHORIZED);
       	}  
    	 else
    	 {
    		 Blogs blog=blogdao.GetBlog(id);
    		 return new ResponseEntity<Blogs>(blog,HttpStatus.OK);
    	 }
     }

}

