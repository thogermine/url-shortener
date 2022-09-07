package dk.lundogbendsen.springbootcourse.urlshortener.security;

import dk.lundogbendsen.springbootcourse.urlshortener.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class TrueSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    UserService userService;

//    @Autowired
//    BCryptPasswordEncoder passwordEncoder;
//
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, MyUserDetailService myUserDetailService, BCryptPasswordEncoder passwordEncoder) throws Exception {
        auth.userDetailsService(myUserDetailService).passwordEncoder(passwordEncoder);
    }


//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("admin").password(bCryptPasswordEncoder().encode("admin")).roles("admin")
//                .and()
//                .withUser("user").password(bCryptPasswordEncoder().encode("user")).roles("user");
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/user").permitAll()
                .antMatchers("/user").hasRole("user")
                .antMatchers("/token/resolve").permitAll()
                .antMatchers("/token", "/token/*").hasRole("user")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .and()
                .httpBasic()
        ;
    }

}
