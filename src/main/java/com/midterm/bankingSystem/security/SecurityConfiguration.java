package com.midterm.bankingSystem.security;

import com.midterm.bankingSystem.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled=true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private UserDetailsServiceImpl userDetailsService;
  
  @Bean
  public PasswordEncoder passwordEncoder () {
    return new BCryptPasswordEncoder();
  }
  
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
      .userDetailsService(userDetailsService)
      .passwordEncoder(passwordEncoder);
  }

  @Override
  public void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.httpBasic();
    httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, "/account/transfer").hasAuthority("ROLE_ACCOUNTUSER")
            .antMatchers(HttpMethod.POST, "/third-party").hasAuthority("ROLE_ADMIN")
            .antMatchers(HttpMethod.GET, "/account/{accountId}/{typeAccount}").hasAuthority("ROLE_ACCOUNTUSER")
            .antMatchers(HttpMethod.POST,"/account-holder").hasAuthority("ROLE_ADMIN")
            .antMatchers(HttpMethod.PATCH, "/account/transaction/**").hasAuthority("ROLE_THIRDPARTY")
            .anyRequest().hasAuthority("ROLE_ADMIN")
            .and().logout().deleteCookies("JSESSIONID");
    httpSecurity.csrf().disable();
  }
}
