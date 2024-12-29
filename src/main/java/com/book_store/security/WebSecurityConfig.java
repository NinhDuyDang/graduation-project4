package com.book_store.security;

import com.book_store.entity.User;
import com.book_store.jwt.JwtAuthenticationFilter;
import com.book_store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.CACHE;
import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.COOKIES;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailServiceImpl userService;
    @Autowired
    UserService userServices;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
//         Get AuthenticationManager Bean
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(userService) // Cung cáp userservice cho spring security
                .passwordEncoder(passwordEncoder()); // cung cấp password encoder
    }

    private static final ClearSiteDataHeaderWriter.Directive[] SOURCE =
            {CACHE, COOKIES};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http
                .authorizeRequests()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/changePassword").authenticated()
                .antMatchers("/myorder").hasAnyRole("CUSTOMER")
                .antMatchers("/myorder/*").hasAnyRole("CUSTOMER")
                .antMatchers("/cart/checkout").hasAnyRole("CUSTOMER")
                .antMatchers("/admin/product").hasAnyRole("STAFF","MANAGER","ADMIN")
                .antMatchers("/admin/home").hasAnyRole("ADMIN")
                .antMatchers("/admin/revenue").hasAnyRole("ADMIN")
                .antMatchers("/admin/listPromotions").hasAnyRole("STAFF","MANAGER","ADMIN")
                .antMatchers("/admin/order").hasAnyRole("STAFF","MANAGER","ADMIN")
                .antMatchers("/admin/category/listCategory").hasAnyRole("STAFF","MANAGER","ADMIN")
                .antMatchers("/admin/customer").hasAnyRole("ADMIN")
                .antMatchers("/admin/staff").hasAnyRole("ADMIN")
                .antMatchers("/admin/admin/category/listCategory/add").hasAnyRole("STAFF", "ADMIN")
                .antMatchers("/admin/listPromotions/add").hasAnyRole("STAFF", "ADMIN")
                .antMatchers("/admin/product/new").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/customer/new").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/newStaff").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/product/saveImage/*").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/product/save").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/customer/save").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/customer/saveEdit").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/saveStaff").hasAnyRole("MANAGER","ADMIN")
                .antMatchers("/admin/saveStaffEdit").hasAnyRole("MANAGER","ADMIN")
                .antMatchers("/admin/listPromotions/saveOrUpdate").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/listPromotions/editAndSave").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/category/listCategory/save").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/category/listCategory/editAndSave").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/saveOrderEdit").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/category/delete/*").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/product/delete/*").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/customer/delete/*").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/deleteStaff/*").hasAnyRole("MANAGER","ADMIN")
                .antMatchers("/admin/delete/*").hasAnyRole("STAFF","ADMIN")
                .antMatchers("/admin/product/deleteImage/*").hasAnyRole("STAFF","ADMIN")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .permitAll()
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/doLogin")
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request,
                                                        HttpServletResponse response,
                                                        AuthenticationException exception) throws IOException, ServletException {
                        User user = userServices.findByUsername(request.getParameter("username"));
                        if(user != null && user.getCustomer().getStatus()==0) {
                            response.sendRedirect("/accountLocked");
                        }else{
                            response.sendRedirect("/loginFail");
                        }
                    }
                })

                .successHandler(new AuthenticationSuccessHandler() {

                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                        Authentication authentication) throws IOException, ServletException {
                        // run custom logics upon successful login
                        User user = userServices.findByUsername(authentication.getName());
                        if(user.getRole().getName().equals("ROLE_ADMIN")
                                ||user.getRole().getName().equals("ROLE_MANAGER")){
                            response.sendRedirect("/admin/home");
                        } else if (user.getRole().getName().equals("ROLE_STAFF")) {
                            response.sendRedirect("/admin/product");
                        } else{
                            response.sendRedirect("/product/listproducts");
                        }
                    }
                })
                .and()
                .logout()
                .permitAll()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/404");
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/check_username")
                .antMatchers("/check_email")
                .antMatchers("/cart/reduceAmount")
                .antMatchers("/css/*", "/js/*", "/images/*")
                .antMatchers("/templates/login.html");
    }
}
