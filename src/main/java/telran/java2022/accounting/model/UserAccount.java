package telran.java2022.accounting.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode(of = {"login"})
@Document(collection = "users")
public class UserAccount {
	@Id
	String login;
	@Setter
	String password;
	@Setter
	String firstName;
	@Setter
	String lastName;
	
	Set<String> roles;
	@Setter
	LocalDate passwordCreateDate;
	
	private static final int COUNT_DAYS_EXPIRED_PASSWORD = 60;

	public UserAccount() {
		passwordCreateDate = LocalDate.now();
		roles = new HashSet<>();
		roles.add("USER");
	}

	public UserAccount(String login, String password, String firstName, String lastName) {
		this();
		this.login = login;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public boolean addRole(String role) {
		return roles.add(role);
	}

	public boolean removeRole(String role) {
		return roles.remove(role);
	}
	
	public boolean isPasswordNonExpired() {
		return ChronoUnit.DAYS.between(passwordCreateDate, LocalDate.now()) <= COUNT_DAYS_EXPIRED_PASSWORD;
	}
}
