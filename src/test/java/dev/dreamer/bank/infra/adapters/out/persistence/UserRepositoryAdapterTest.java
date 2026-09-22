package dev.dreamer.bank.infra.adapters.out.persistence;

import dev.dreamer.bank.TestcontainersConfiguration;
import dev.dreamer.bank.domain.models.User;
import dev.dreamer.bank.domain.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
@Transactional
class UserRepositoryAdapterTest {

    @Autowired
    private UserRepositoryPort userRepository;

    @Test
    @DisplayName("Should save a user and generate an ID")
    void shouldSaveUser() {
        User user = new User(null, "John Doe", "john.doe@example.com", "hashed_pwd_123");

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john.doe@example.com", savedUser.getEmail());
        assertEquals("hashed_pwd_123", savedUser.getPasswordHash());
    }

    @Test
    @DisplayName("Should return true when email exists and false when it does not")
    void shouldCheckIfExistsByEmail() {
        User user = new User(null, "Jane Doe", "jane.doe@example.com", "hashed_pwd_123");
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("jane.doe@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindByEmail() {
        User user = new User(null, "Bob Smith", "bob.smith@example.com", "hashed_pwd_123");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("bob.smith@example.com");

        assertTrue(found.isPresent());
        assertEquals("Bob Smith", found.get().getName());
        assertEquals("bob.smith@example.com", found.get().getEmail());

        Optional<User> notFound = userRepository.findByEmail("nobody@example.com");
        assertTrue(notFound.isEmpty());
    }

    @Test
    @DisplayName("Should find user by id")
    void shouldFindById() {
        User user = new User(null, "Alice Wonderland", "alice@example.com", "hashed_pwd_123");
        User savedUser = userRepository.save(user);

        Optional<User> found = userRepository.findById(savedUser.getId());

        assertTrue(found.isPresent());
        assertEquals(savedUser.getId(), found.get().getId());
        assertEquals("Alice Wonderland", found.get().getName());

        Optional<User> notFound = userRepository.findById("00000000-0000-0000-0000-000000000000");
        assertTrue(notFound.isEmpty());
    }
}
