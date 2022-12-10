package telran.java2022.accounting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import telran.java2022.accounting.dao.UserAccountRepository;
import telran.java2022.accounting.dto.RolesResponseDto;
import telran.java2022.accounting.dto.UserAccountResponseDto;
import telran.java2022.accounting.dto.UserRegisterDto;
import telran.java2022.accounting.dto.UserUpdateDto;
import telran.java2022.accounting.dto.exceptions.UserExistsException;
import telran.java2022.accounting.dto.exceptions.UserNotFoundException;
import telran.java2022.accounting.model.UserAccount;

@ExtendWith(MockitoExtension.class)
public class UserAccountServiceTests {

	@Mock
	UserAccountRepository repository;

	@Spy
	ModelMapper modelMapper;

	@Mock
	PasswordEncoder passwordEncoder;

	@InjectMocks
	UserAccountServiceImpl service;
	
	UserAccount accountBefore;
	
	UserRegisterDto userRegisterDtoBefore;
	
	UserUpdateDto userUpdateDto;
	
	String testRole;

	@BeforeEach
	public void setup() {
		accountBefore = new UserAccount("Test", "1234", "TestFirstName", "TestLastName");
		
		userRegisterDtoBefore = modelMapper.map(accountBefore, UserRegisterDto.class);
		
		testRole = "TEST_ROLE";
		
		userUpdateDto = new UserUpdateDto();
		userUpdateDto.setFirstName("Max");
		userUpdateDto.setLastName("NewTest");
	}

	@Test
	void should_add_one_user() {
		when(repository.save(any(UserAccount.class))).thenReturn(accountBefore);	
		UserAccountResponseDto actualAccount = service.addUser(userRegisterDtoBefore);	
		assertThat(actualAccount.getLogin()).isEqualTo(accountBefore.getLogin());
	}

	@Test
	void should_no_add_same_user() {
		when(repository.existsById(accountBefore.getLogin())).thenReturn(true);
		Assertions.assertThrows(UserExistsException.class, () -> service.addUser(userRegisterDtoBefore));
	}

	@Test
	public void should_get_one_user() {
		when(repository.findById(accountBefore.getLogin())).thenReturn(Optional.of(accountBefore));

		UserAccountResponseDto actualAccount = service.getUser(accountBefore.getLogin());
		assertThat(actualAccount.getLogin()).isEqualTo(accountBefore.getLogin());
	}
	
	@Test
	public void should_not_get_user_that_not_exists() {
		when(repository.findById(accountBefore.getLogin())).thenReturn(Optional.empty());
		Assertions.assertThrows(UserNotFoundException.class, () -> service.getUser(accountBefore.getLogin()));
	}
	
	@Test
    void should_remove_one_user() {
		when(repository.findById(accountBefore.getLogin())).thenReturn(Optional.of(accountBefore));
		doNothing().when(repository).deleteById(any(String.class));
		service.removeUser(accountBefore.getLogin());
	    verify(repository, times(1)).deleteById(any(String.class));
	}
	
	@Test
    void should_not_remove_one_user_that_not_exists() {
		when(repository.findById(accountBefore.getLogin())).thenReturn(Optional.empty());
		Assertions.assertThrows(UserNotFoundException.class, () -> service.removeUser(accountBefore.getLogin()));
	}
	
	@Test
    void should_edit_one_user() {
		when(repository.findById(accountBefore.getLogin())).thenReturn(Optional.of(accountBefore));
		
		UserAccountResponseDto actualUser = service.editUser("Test", userUpdateDto);
		
		assertThat(actualUser.getLogin()).isEqualTo(accountBefore.getLogin());
		assertThat(actualUser.getFirstName()).isEqualTo(accountBefore.getFirstName());
		assertThat(actualUser.getLastName()).isEqualTo(accountBefore.getLastName());
	}
	
	@Test
    void should_not_edit_user_that_not_exists() {
		when(repository.findById(accountBefore.getLogin())).thenReturn(Optional.empty());
		Assertions.assertThrows(UserNotFoundException.class, 
								() -> service.editUser(accountBefore.getLogin(), userUpdateDto));
	}
	
	@Test
    void should_change_role_list() {
		when(repository.findById(accountBefore.getLogin())).thenReturn(Optional.of(accountBefore));
		
		RolesResponseDto expectedUser = service.changeRolesList(accountBefore.getLogin(), testRole, true);
		
		assertThat(expectedUser.getLogin()).isEqualTo(accountBefore.getLogin());
		assertThat(expectedUser.getRoles().contains(testRole)).isEqualTo(true);
	}
	
	@Test
    void should_not_change_role_list_of_user_that_not_exists() {
		when(repository.findById(accountBefore.getLogin())).thenReturn(Optional.empty());
		Assertions.assertThrows(UserNotFoundException.class, 
								() -> service.changeRolesList(accountBefore.getLogin(), testRole, true));
	}
	
	@Test
    void should_delete_role_from_role_list() {
		when(repository.findById(accountBefore.getLogin())).thenReturn(Optional.of(accountBefore));
		String roleForDelete = "USER";
			
		RolesResponseDto expectedUser = service.changeRolesList(accountBefore.getLogin(), roleForDelete, false);
	
		assertThat(expectedUser.getLogin()).isEqualTo(accountBefore.getLogin());
		assertThat(expectedUser.getRoles().contains(roleForDelete)).isEqualTo(false);
	}
	
	@Test
    void should_change_password() {
		when(repository.findById(accountBefore.getLogin())).thenReturn(Optional.of(accountBefore));
		String newPassword = "4321";
		
		when(repository.save(any(UserAccount.class))).thenReturn(accountBefore);
		service.changePassword(accountBefore.getLogin(), newPassword);
	
		verify(repository, times(1)).save(any(UserAccount.class));
	}
	
	@Test
    void should_not_change_password_of_user_that_not_exists() {
		when(repository.findById(any(String.class))).thenReturn(Optional.empty());
		Assertions.assertThrows(UserNotFoundException.class, 
								() -> service.changePassword(accountBefore.getLogin(), any(String.class)));
	}
}
