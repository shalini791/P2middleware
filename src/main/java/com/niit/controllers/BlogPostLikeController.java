package com.niit.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.dao.BlogDao;
import com.niit.dao.BlogPostLikesDao;
import com.niit.model.BlogPostLikes;
import com.niit.model.Blogs;
import com.niit.model.ErrorClazz;

@RestController
public class BlogPostLikeController {
	@Autowired
	private BlogPostLikesDao blogpostlikedao;
	@Autowired
	private BlogDao blogdao;
	
	 public BlogPostLikeController()
	 {
		System.out.println("BlogPostLikeController class  Instantiated");
	 }
	 
	 @RequestMapping(value="/Likeblog/{blogid}",method=RequestMethod.GET)
	private ResponseEntity<?> Like_DislikeBlog(@PathVariable int blogid,HttpSession session)
	{
	System.out.println("In BlogPostLikeController Like_DislikeBlog function invoked");
	
	if(session.getAttribute("email")==null)
	{
		ErrorClazz ec=new ErrorClazz(75,"Please Login");
		return new ResponseEntity<ErrorClazz>(ec,HttpStatus.UNAUTHORIZED);
	}
	else
	{  
		String email=(String) session.getAttribute("email");
		BlogPostLikes blogpostlikes=blogpostlikedao.HasUserLikedBlogPost(blogid, email);
		if(blogpostlikes==null)//User has Liked the BlogPost
		{   
			blogpostlikes=new BlogPostLikes();
			Blogs blog=blogdao.GetBlog(blogid);
			int likes=blog.getLikes()+1;
			blog.setLikes(likes);
			blogdao.UpdateBlogPost(blog);
			blogpostlikes.setBlogid(blogid);
			blogpostlikes.setLikeby(email);
			blogpostlikedao.LikeBlogPost(blogpostlikes);
			return new ResponseEntity<BlogPostLikes>(blogpostlikes,HttpStatus.OK);
		}
		else//User Has Disliked the BlogPost
		{
			Blogs blog=blogdao.GetBlog(blogid);
			int likes=blog.getLikes()-1;
			blog.setLikes(likes);
			blogdao.UpdateBlogPost(blog);
			blogpostlikedao.RemoveBlogPostLike(blogid, email);
			return new ResponseEntity<BlogPostLikes>(blogpostlikes,HttpStatus.OK);
		}
		
	}
	}
	
	
	@RequestMapping(value="/Getalllikedblogs",method=RequestMethod.GET)
	public ResponseEntity<?>GetAllLikedBlogs(HttpSession session)
	{
		System.out.println("In BlogPostLikeController GetAllLikedBlogs function invoked");
		if(session.getAttribute("email")==null)
		{
			ErrorClazz ec=new ErrorClazz(75,"Please Login");
			return new ResponseEntity<ErrorClazz>(ec,HttpStatus.UNAUTHORIZED);
		}
		else
		{  
			String email=(String) session.getAttribute("email");
			List<BlogPostLikes>AllLikedBlogs=blogpostlikedao.GetAllLikedBlogs(email);
			return new ResponseEntity<List<BlogPostLikes>>(AllLikedBlogs,HttpStatus.OK);
		}
	}

}
