package app.spring.tax.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
                .antMatchers().permitAll()
                .antMatchers(HttpMethod.GET, "/admin/taxes").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/admin/tax/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/admin/tax").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/admin/tax/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/admin/tax/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/user/taxes").authenticated()
                .antMatchers(HttpMethod.GET, "/user/tax/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/user/tax/**").authenticated()
                .antMatchers(HttpMethod.POST, "/user/payTax/**").authenticated()
            .and()
            .formLogin().disable()
            .addFilterBefore(new OncePerRequestFilter() {
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
                        UserDetails userDetails = userService.loadUserByUsername(username);
                        if (jwtUtil.validateToken(jwt, userDetails)) {
                            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    }
                    chain.doFilter(request, response);
                }
            }, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
