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

@Configuration
public class SecurityConfiguration {

    private final DataSource dataSource;

    public SecurityConfiguration(DataSource dataSource){
        this.dataSource=dataSource;
    }

    @Bean
    public UserDetailsService userDetailsService(){
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery("SELECT username, password, 1 as enabled FROM utente WHERE username=?");
        manager.setAuthoritiesByUsernameQuery("SELECT username, role FROM utente WHERE username=?");
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception{

        httpSecurity.authorizeHttpRequests(authorize->{
            authorize.requestMatchers(HttpMethod.GET, "/",
                    "/index",
                    "/register",
                    "/css/**",
                    "/esercizi",
                    "/esercizi/{id}",
                    "/routines", //senza crearlo nel controller darà "/error" che è visualizzabile solo da autenticato e mostrerà la pagina di login
                    "/api/routines/{id}",// Tutti possono vedere delle routine es. api/routines/5
                    "/images/**",
                    "/favicon.ico").permitAll();
            authorize.requestMatchers(HttpMethod.POST, "/register", "/login").permitAll();
            authorize.requestMatchers("/api/**","/esercizi/**").authenticated(); //qualsiasi operazione se non la visualizzazione
            authorize.requestMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(ADMIN_ROLE);
            authorize.requestMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(ADMIN_ROLE);
            authorize.anyRequest().authenticated();
        });

        /*tutte le azioni "/esercizi/**" richiedono autorizzazione tranne "/esercizi/{id}", pure "/esercizi/{id}/edit" è protetto da "/esercizi/** "*/


        /*/Login non ha bisogno di un @PostMapping perché Spring Security ha già un filtro che gestisce proprio quella richiesta,
        * perché la sicurezza deve essere controllata prima che la richiesta arrivi ai controller*/
        httpSecurity.formLogin(form ->{
           form.loginPage("/login").permitAll();
           form.defaultSuccessUrl("/",true);
           form.failureUrl("/login?error=true");
        });

        httpSecurity.logout(logout ->{
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
