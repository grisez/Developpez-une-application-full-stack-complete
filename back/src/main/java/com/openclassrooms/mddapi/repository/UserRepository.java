package com.openclassrooms.mddapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

}
