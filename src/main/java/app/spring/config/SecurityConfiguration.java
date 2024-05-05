package app.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import app.spring.services.UserService;
import app.spring.util.JwtRequestFilter;

@EnableWebSecurity

public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
    private UserService userService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
                .antMatchers("/auth/register", "/auth/login").permitAll()
                .antMatchers(HttpMethod.GET, "/admin/taxes").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/admin/tax/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/admin/tax").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/admin/tax/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/admin/tax/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/user/taxes").authenticated()
                .antMatchers(HttpMethod.GET, "/user/tax/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/user/tax/**").authenticated()
                .antMatchers(HttpMethod.POST, "/user/payTax/**").authenticated()
                .anyRequest().authenticated()
            .and()
            .formLogin().disable();

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
