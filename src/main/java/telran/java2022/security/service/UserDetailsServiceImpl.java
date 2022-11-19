package telran.java2022.security.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java2022.accounting.dao.UserAccountRepository;
import telran.java2022.accounting.dto.exceptions.UserNotFoundException;
import telran.java2022.accounting.model.UserAccount;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	
	final UserAccountRepository repository;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		UserAccount userAccount = repository.findById(userName).orElseThrow(() -> new UserNotFoundException());
		String[] roles = userAccount.getRoles().stream()
													.map(r -> "ROLE_" + r.toUpperCase())
													.toArray(String[]::new);
		

		return new User(userName, userAccount.getPassword(), AuthorityUtils.createAuthorityList(roles));
		//return new User(userName, userAccount.getPassword(), true, true, 
		//		userAccount.isPasswordNonExpired(), true, AuthorityUtils.createAuthorityList(roles));
	}

}
