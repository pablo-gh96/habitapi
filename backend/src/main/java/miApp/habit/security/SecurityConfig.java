package miApp.habit.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final JwtAuthFilter jwtFilter;

  public SecurityConfig(JwtAuthFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      // 🚫 Sin sesiones, solo JWT
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      // ❌ CSRF no aplica con JWT
      .csrf(csrf -> csrf.disable())
      // ✅ CORS habilitado (ya configurado en tu clase o global)
      .cors(Customizer.withDefaults())
      // 🔒 Rutas protegidas
      .authorizeHttpRequests(auth -> auth
          .requestMatchers("/auth/login", "/auth/signup", "/auth/refresh", "/actuator/health").permitAll()
          .anyRequest().authenticated()
      )
      // 🔐 Filtro JWT antes del de login por username/password
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}

