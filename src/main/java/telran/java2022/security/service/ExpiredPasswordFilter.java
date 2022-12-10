package telran.java2022.security.service;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

@Service
public class ExpiredPasswordFilter extends GenericFilterBean {
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		Principal principal = request.getUserPrincipal();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		try {
			 String currentUserName = authentication.getName();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		if (principal != null && checkEndPoint(request.getMethod(), request.getServletPath())) {
			//UserProfile userProfile = (UserProfile) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			
			//if(!userDetails.isCredentialsNonExpired()) {
				//response.sendError(403, "password expired");
				//return;
			//}
			
			//if(!userProfile.isPasswordNotExpired()) {
			//	response.sendError(403, "password expired");
			//	return;
			//}
		}
		chain.doFilter(request, response);
	}

	private boolean checkEndPoint(String method, String path) {
		return !("Put".equalsIgnoreCase(method) && path.matches("/account/password/?"));
	}

}