package it.uniroma3.siw.wefit.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static it.uniroma3.siw.wefit.model.Utente.ADMIN_ROLE;
import static it.uniroma3.siw.wefit.model.Utente.DEFAULT_ROLE;

@Configuration
public class SecurityConfiguration {

    private final DataSource dataSource;

    public SecurityConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery("SELECT username, password, 1 as enabled FROM utente WHERE username=?");
        manager.setAuthoritiesByUsernameQuery("SELECT username, role FROM utente WHERE username=?");
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers("/admin", "/admin/**").hasAuthority(ADMIN_ROLE);
            authorize.requestMatchers("/area-personale", "/area-personale/**").hasAuthority(DEFAULT_ROLE);
            // Queste rotte devono precedere /routines/{id} e /esercizi/{id}, che altrimenti intercetterebbero "new".
            authorize.requestMatchers(HttpMethod.GET, "/routines/new", "/esercizi/new").authenticated();
            authorize.requestMatchers(HttpMethod.GET, "/", "/index", "/register", "/login", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/esercizi", "/esercizi/{id}", "/routines", "/routines/{id}", "/routines/{id}/esercizi", "/api/routines/{id}").permitAll();
            authorize.requestMatchers(HttpMethod.POST, "/register", "/login").permitAll();
            authorize.anyRequest().authenticated();
        });

        /*/Login non ha bisogno di un @PostMapping perché Spring Security ha già un filtro che gestisce proprio quella richiesta,
         * perché la sicurezza deve essere controllata prima che la richiesta arrivi ai controller*/
        httpSecurity.formLogin(form -> {
            form.loginPage("/login").permitAll();
            form.defaultSuccessUrl("/", true);
            form.failureUrl("/login?error=true");
        });

        httpSecurity.logout(logout -> {
            logout.logoutUrl("/logout");
            logout.logoutSuccessUrl("/");
            logout.invalidateHttpSession(true);
            logout.deleteCookies("JSESSIONID");
            logout.clearAuthentication(true);
            logout.permitAll();
        });

        return httpSecurity.build();
    }
}
