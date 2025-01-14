package cat.copernic.CarConnect.Security;

import cat.copernic.CarConnect.Entity.MySQL.Agent;
import cat.copernic.CarConnect.Entity.MySQL.Enums.Rol;
import cat.copernic.CarConnect.Entity.MySQL.Enums.TipusPermis;
import cat.copernic.CarConnect.Repository.MySQL.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * @author david
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private AgentRepository agentRepo;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login/**", "/Registre/**", "/index").permitAll() // Acceso público
                .requestMatchers("/styles/**", "/scripts/**").permitAll()   // Acceso público a recursos estáticos
                
                .requestMatchers("/reservas/create").hasAnyAuthority(TipusPermis.CREAR_RESERVES.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/reservas/edit/**").hasAnyAuthority(TipusPermis.EDITAR_RESERVES.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/reservas/delete/**").hasAnyAuthority(TipusPermis.ELIMINAR_RESERVES.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/reservas/list").hasAnyAuthority(TipusPermis.LLISTAR_RESERVES.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/reservas").hasAnyAuthority(TipusPermis.LLISTAR_RESERVES.toString(),TipusPermis.ADMIN.toString())
                //.requestMatchers("/vehicles/selected/**").hasAnyAuthority(TipusPermis.CREAR_RESERVES.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/reservas/selected/**").hasAnyAuthority(TipusPermis.CREAR_RESERVES.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/reservas/pagament").hasAnyAuthority(TipusPermis.CREAR_RESERVES.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/reservas/guardar").hasAnyAuthority(TipusPermis.CREAR_RESERVES.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/reservas/confirmacion").hasAnyAuthority(TipusPermis.CREAR_RESERVES.toString(),TipusPermis.ADMIN.toString())
                    
                .requestMatchers("/vehicles/create").hasAnyAuthority(TipusPermis.CREAR_VEHICLE.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/vehicles/edit/**").hasAnyAuthority(TipusPermis.EDITAR_VEHICLE.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/vehicles/return/**").hasAnyAuthority(TipusPermis.EDITAR_VEHICLE.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/vehicles/deliver/**").hasAnyAuthority(TipusPermis.EDITAR_VEHICLE.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/vehicles/delete/**").hasAnyAuthority(TipusPermis.ELIMINAR_VEHICLE.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/vehicles/list").hasAnyAuthority(TipusPermis.LLISTAR_VEHICLE.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/vehicles/models/**").hasAnyAuthority(TipusPermis.CREAR_VEHICLE.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/vehicles/selected/**").permitAll()
                .requestMatchers("/vehicles").hasAnyAuthority(TipusPermis.LLISTAR_VEHICLE.toString(),TipusPermis.ADMIN.toString())
                    
                .requestMatchers("/incidencias/create/**").hasAnyAuthority(TipusPermis.CREAR_INCIDENCIES.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/incidencias/edit/**").hasAnyAuthority(TipusPermis.EDITAR_INCIDENCIES.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/incidencias/desactivate/**").hasAnyAuthority(TipusPermis.ELIMINAR_INCIDENCIES.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/incidencias/list").hasAnyAuthority(TipusPermis.LLISTAR_INCIDENCIES.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/incidencias/**").hasAnyAuthority(TipusPermis.LLISTAR_INCIDENCIES.toString(),TipusPermis.ADMIN.toString())
                    
                .requestMatchers("/clients").hasAnyAuthority(TipusPermis.LLISTAR_CLIENT.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/clients/filter").hasAnyAuthority(TipusPermis.LLISTAR_CLIENT.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/clients/create").hasAnyAuthority(TipusPermis.CREAR_CLIENT.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/clients/edit/**").hasAnyAuthority(TipusPermis.EDITAR_CLIENT.toString(),TipusPermis.ADMIN.toString())  
                .requestMatchers("/clients/delete/**").hasAnyAuthority(TipusPermis.ELIMINAR_CLIENT.toString(),TipusPermis.ADMIN.toString())
                
                .requestMatchers("/agents").hasAnyAuthority(TipusPermis.LLISTAR_AGENT.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/agents/filter").hasAnyAuthority(TipusPermis.LLISTAR_AGENT.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/agents/create").hasAnyAuthority(TipusPermis.CREAR_AGENT.toString(),TipusPermis.ADMIN.toString())
                .requestMatchers("/agents/edit/**").hasAnyAuthority(TipusPermis.EDITAR_AGENT.toString(),TipusPermis.ADMIN.toString())  
                .requestMatchers("/agents/delete/**").hasAnyAuthority(TipusPermis.ELIMINAR_AGENT.toString(),TipusPermis.ADMIN.toString())
                
                .requestMatchers("/historic/**").hasAnyAuthority(TipusPermis.HISTORIC_INCIDENCIES.toString(),TipusPermis.HISTORIC_RESERVES.toString(),TipusPermis.ADMIN.toString())
                
                .requestMatchers("/**").hasAuthority(TipusPermis.ADMIN.toString())
                    
                .anyRequest().authenticated()             // Acceso restringido
            )
            .formLogin(form -> form
                .loginPage("/login")                      // Página de login personalizada
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout.permitAll());
            
            
            
        return http.build();
    }
    
    @Bean
    public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(new ValidadorUsuaris())
            .passwordEncoder(passwordEncoder)            
            .and()
            .build();
    
    }
   
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    

    
}
