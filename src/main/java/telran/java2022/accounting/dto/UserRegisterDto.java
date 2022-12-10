package telran.java2022.accounting.dto;


import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class UserRegisterDto {
	String login;
	String password;
	String firstName;
	String lastName;
}
