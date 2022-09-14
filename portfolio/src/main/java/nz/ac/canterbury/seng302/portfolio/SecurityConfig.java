package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.authentication.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Configures the security elements of the application to ensure only verified users can use it
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines the login and logout pages, as well as setting the basic security options
     * @param security Http security object containing settings required for spring security
     * @return Completed security object
     * @throws Exception
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity security) throws Exception
    {
        // Force authentication for all endpoints except /login
        security
            .addFilterBefore(new JwtAuthenticationFilter(), BasicAuthenticationFilter.class)
                .authorizeRequests()
                    .antMatchers("/login*").permitAll()
                    .anyRequest()
                    .authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login").permitAll()
                .and()
                .logout()
                    .logoutSuccessUrl("/login")
                    .permitAll()
                    .invalidateHttpSession(true)
                    .deleteCookies("lens-session-token")
                .and()
                    .exceptionHandling().accessDeniedPage("/login");
                    

        security.cors();
        security.csrf().disable();
        security.httpBasic().disable();
        return security.build();
    }

    /**
     * Adds the bypasses to pages allowing the user to access the register and login page
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer()
    {
        return web -> web.ignoring().antMatchers("/login", "/register", "/css/**", "/icons/**", "/javascript/**");
    }
}
