package Him.admin.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable()) // disable CSRF for testing with Postman
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/users/**").permitAll()
                            .requestMatchers("/api/login/**").permitAll()// ✅ allow user creation
                            .requestMatchers("/api/branches/**").permitAll() //for development and testing with postman only
                            .anyRequest().authenticated()
                    );

            return http.build();
        }


}
