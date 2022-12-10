package telran.java2022.security.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.SessionManagementFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class AuthorizationConfiguration {
	
	final ExpiredPasswordFilter expiredPasswordFilter;

	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http.httpBasic();
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.addFilterAfter(expiredPasswordFilter, ExceptionTranslationFilter  .class);
		http.authorizeRequests(authorize -> authorize
						.mvcMatchers("/account/register", "/forum/posts/**").permitAll()
						.mvcMatchers("/account/user/*/role/*").hasRole("ADMINISTRATOR")
						.mvcMatchers(HttpMethod.PUT, "/account/user/{login}").access("#login == authentication.name")
						.mvcMatchers(HttpMethod.DELETE, "/account/user/{login}").access("#login == authentication.name or hasRole('ADMINISTRATOR')")
						.mvcMatchers(HttpMethod.POST, "/forum/post/{author}").access("#author == authentication.name")
						.mvcMatchers(HttpMethod.PUT, "/forum/post/{id}/comment/{author}").access("#author == authentication.name")
						.mvcMatchers(HttpMethod.PUT, "/forum/post/{id}").access("@customSecurity.checkPostAuthor(#id, authentication.name)")
						.mvcMatchers(HttpMethod.DELETE, "/forum/post/{id}").access("@customSecurity.checkPostAuthor(#id, authentication.name) or hasRole('MODERATOR')")
						.anyRequest().authenticated());
		return http.build();
	}
}
