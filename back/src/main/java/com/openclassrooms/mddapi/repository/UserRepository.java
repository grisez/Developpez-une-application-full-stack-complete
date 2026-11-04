package com.openclassrooms.mddapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openclassrooms.mddapi.exception.EmailAlreadyUsedException;
import com.openclassrooms.mddapi.exception.UserNotFoundException;
import com.openclassrooms.mddapi.exception.UsernameAlreadyUsedException;
import com.openclassrooms.mddapi.model.User;

/**
 * Data access to users. Comparisons are case-insensitive, following the collation of the {@code users} table.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

	/** Whether another account than {@code id} already uses this email. */
	boolean existsByEmailAndIdNot(String email, Long id);

	/** Whether another account than {@code id} already uses this username. */
	boolean existsByUsernameAndIdNot(String username, Long id);

	/**
	 * Returns the user with this id.
	 *
	 * @param id the user id
	 * @return the user
	 * @throws UserNotFoundException if no user has this id
	 */
	default User getExisting(Long id) {
		return findById(id).orElseThrow(() -> new UserNotFoundException(id));
	}

	/**
	 * Checks that no other account already uses this email or this username.
	 *
	 * @param email     the normalized email
	 * @param username  the username
	 * @param accountId the account being updated, or {@code null} for an account being created
	 * @throws EmailAlreadyUsedException    if another account uses the email
	 * @throws UsernameAlreadyUsedException if another account uses the username
	 */
	default void checkAvailable(String email, String username, Long accountId) {
		if (accountId == null ? existsByEmail(email) : existsByEmailAndIdNot(email, accountId)) {
			throw new EmailAlreadyUsedException();
		}
		if (accountId == null ? existsByUsername(username) : existsByUsernameAndIdNot(username, accountId)) {
			throw new UsernameAlreadyUsedException();
		}
	}

}
