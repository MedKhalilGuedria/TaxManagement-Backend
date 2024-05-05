package app.spring.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import app.spring.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtRequestFilter  extends OncePerRequestFilter {
	
	 @Autowired
	    private JwtUtil jwtUtil;

	    @Autowired
	    private UserService userService;

	    @Override
	    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	            throws ServletException, IOException {
	        final String authorizationHeader = request.getHeader("Authorization");

	        String username = null;
	        String jwt = null;

	        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
	            jwt = authorizationHeader.substring(7);
	            try {
	                username = jwtUtil.extractUsername(jwt);
	            } catch (ExpiredJwtException e) {
	                logger.error("JWT token expired");
	            } catch (Exception e) {
	                logger.error("Error parsing JWT token");
	            }
	        }

	        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	            UserDetails userDetails = this.userService.loadUserByUsername(username);
	            if (jwtUtil.validateToken(jwt, userDetails)) {
	                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
	                        userDetails, null, userDetails.getAuthorities());
	                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	            }
	        }
	        chain.doFilter(request, response);
	    }

}
