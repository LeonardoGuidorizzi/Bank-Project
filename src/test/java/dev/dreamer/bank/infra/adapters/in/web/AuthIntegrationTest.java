package dev.dreamer.bank.infra.adapters.in.web;

import dev.dreamer.bank.TestcontainersConfiguration;
import dev.dreamer.bank.domain.models.User;
import dev.dreamer.bank.domain.ports.out.HashPort;
import dev.dreamer.bank.domain.ports.out.UserRepositoryPort;
import dev.dreamer.bank.infra.adapters.out.security.JwtTokenAdapter;
import dev.dreamer.bank.infra.security.JsonAuthenticationEntryPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
@Transactional
class AuthIntegrationTest {
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private HashPort hashPort;

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    // ---------- helpers ----------

    private ResultActions register(String username, String email, String password) throws Exception {
        return mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(username, email, password)));
    }

    private ResultActions login(String login, String password) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":%s,\"password\":%s}".formatted(quote(login), quote(password))));
    }

    private String loginAndGetToken(String login, String password) throws Exception {
        MvcResult result = login(login, password).andExpect(status().isOk()).andReturn();
        Matcher matcher = TOKEN_PATTERN.matcher(result.getResponse().getContentAsString());
        assertTrue(matcher.find(), "response should contain a token");
        return matcher.group(1);
    }

    private ResultActions getMe(String authorizationHeader) throws Exception {
        var request = get("/api/users/me");
        if (authorizationHeader != null) {
            request.header("Authorization", authorizationHeader);
        }
        return mockMvc.perform(request);
    }

    private static String json(String username, String email, String password) {
        return "{\"username\":%s,\"email\":%s,\"password\":%s}"
                .formatted(quote(username), quote(email), quote(password));
    }

    private static String quote(String value) {
        return value == null ? "null" : "\"" + value + "\"";
    }

    // ---------- sign-up ----------

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Test
        @DisplayName("Should create user with default CUSTOMER role and never return the password")
        void shouldRegisterWithDefaultRole() throws Exception {
            register("john", "john@example.com", "secret123")
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", not(emptyOrNullString())))
                    .andExpect(jsonPath("$.username").value("john"))
                    .andExpect(jsonPath("$.email").value("john@example.com"))
                    .andExpect(jsonPath("$.role").value("CUSTOMER"))
                    .andExpect(jsonPath("$.password").doesNotExist())
                    .andExpect(jsonPath("$.passwordHash").doesNotExist());
        }

        @Test
        @DisplayName("Should store the password only as a BCrypt hash")
        void shouldStorePasswordHashed() throws Exception {
            register("john", "john@example.com", "secret123").andExpect(status().isCreated());

            User stored = userRepository.findByEmail("john@example.com").orElseThrow();
            assertNotEquals("secret123", stored.getPasswordHash());
            assertTrue(stored.getPasswordHash().startsWith("$2"));
            assertTrue(hashPort.matches("secret123", stored.getPasswordHash()));
        }

        @Test
        @DisplayName("Should reject a duplicate e-mail with a clear message")
        void shouldRejectDuplicateEmail() throws Exception {
            register("john", "john@example.com", "secret123").andExpect(status().isCreated());

            register("other", "john@example.com", "secret123")
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("E-mail já cadastrado"));
        }

        @Test
        @DisplayName("Should reject a duplicate e-mail written with different case")
        void shouldRejectDuplicateEmailDifferentCase() throws Exception {
            register("john", "john@example.com", "secret123").andExpect(status().isCreated());

            register("other", "JOHN@Example.COM", "secret123")
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("E-mail já cadastrado"));
        }

        @Test
        @DisplayName("Should store e-mail in lower case")
        void shouldNormalizeEmail() throws Exception {
            register("john", "John.Doe@Example.COM", "secret123")
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value("john.doe@example.com"));
        }

        @Test
        @DisplayName("Should reject a duplicate username")
        void shouldRejectDuplicateUsername() throws Exception {
            register("john", "john@example.com", "secret123").andExpect(status().isCreated());

            register("john", "another@example.com", "secret123")
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Nome de usuário já cadastrado"));
        }

        @Test
        @DisplayName("Should reject a password that is too short")
        void shouldRejectShortPassword() throws Exception {
            register("john", "john@example.com", "12345")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fields.password", containsString("6")));
        }

        @Test
        @DisplayName("Should reject null fields")
        void shouldRejectNullFields() throws Exception {
            register(null, null, null)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fields.username").exists())
                    .andExpect(jsonPath("$.fields.email").exists())
                    .andExpect(jsonPath("$.fields.password").exists());
        }

        @Test
        @DisplayName("Should reject empty fields")
        void shouldRejectEmptyFields() throws Exception {
            register("", "", "")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fields.username").exists())
                    .andExpect(jsonPath("$.fields.email").exists())
                    .andExpect(jsonPath("$.fields.password").exists());
        }

        @Test
        @DisplayName("Should reject an invalid e-mail")
        void shouldRejectInvalidEmail() throws Exception {
            register("john", "not-an-email", "secret123")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fields.email").exists());
        }

        @Test
        @DisplayName("Should reject a missing body")
        void shouldRejectMissingBody() throws Exception {
            mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    // ---------- login ----------

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("Should return a Bearer token for valid credentials")
        void shouldLoginWithValidCredentials() throws Exception {
            register("john", "john@example.com", "secret123").andExpect(status().isCreated());

            login("john@example.com", "secret123")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token", not(emptyOrNullString())))
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.expiresIn").value(3600));
        }

        @Test
        @DisplayName("Should log in by username and by e-mail with different case")
        void shouldLoginByUsernameAndCaseInsensitiveEmail() throws Exception {
            register("john", "john@example.com", "secret123").andExpect(status().isCreated());

            login("john", "secret123").andExpect(status().isOk());
            login("JOHN@EXAMPLE.COM", "secret123").andExpect(status().isOk());
        }

        @Test
        @DisplayName("Wrong password and unknown user return the same generic 401")
        void shouldReturnGenericErrorForInvalidCredentials() throws Exception {
            register("john", "john@example.com", "secret123").andExpect(status().isCreated());

            String wrongPassword = login("john@example.com", "wrong-password")
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Credenciais inválidas"))
                    .andReturn().getResponse().getContentAsString();

            String unknownUser = login("ghost@example.com", "secret123")
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Credenciais inválidas"))
                    .andReturn().getResponse().getContentAsString();

            // Same body apart from the timestamp
            assertEquals(wrongPassword.replaceAll("\"timestamp\":\"[^\"]*\"", ""),
                    unknownUser.replaceAll("\"timestamp\":\"[^\"]*\"", ""));
        }

        @Test
        @DisplayName("Should return 401 (not 500) for a password longer than BCrypt's limit")
        void shouldRejectVeryLongPassword() throws Exception {
            register("john", "john@example.com", "secret123").andExpect(status().isCreated());

            login("john@example.com", "a".repeat(100))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should reject empty credentials")
        void shouldRejectEmptyCredentials() throws Exception {
            login("", "")
                    .andExpect(status().isBadRequest());
        }
    }

    // ---------- protected routes ----------

    @Nested
    @DisplayName("Protected route GET /api/users/me")
    class ProtectedRoute {

        @Test
        @DisplayName("Should allow access with a valid token")
        void shouldAllowValidToken() throws Exception {
            register("john", "john@example.com", "secret123").andExpect(status().isCreated());
            String token = loginAndGetToken("john@example.com", "secret123");

            getMe("Bearer " + token)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("john"))
                    .andExpect(jsonPath("$.email").value("john@example.com"))
                    .andExpect(jsonPath("$.role").value("CUSTOMER"));
        }

        @Test
        @DisplayName("Should reject a request without a token")
        void shouldRejectMissingToken() throws Exception {
            getMe(null)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(JsonAuthenticationEntryPoint.MESSAGE));
        }

        @Test
        @DisplayName("Should reject an expired token")
        void shouldRejectExpiredToken() throws Exception {
            User saved = userRepository.save(
                    new User(null, "john", "john@example.com", hashPort.hash("secret123")));
            Clock twoHoursAgo = Clock.fixed(Instant.now().minus(Duration.ofHours(2)), ZoneOffset.UTC);
            String expiredToken = new JwtTokenAdapter(jwtSecret, Duration.ofHours(1).toMillis(), twoHoursAgo)
                    .generateToken(saved);

            getMe("Bearer " + expiredToken)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should reject a tampered token")
        void shouldRejectTamperedToken() throws Exception {
            register("john", "john@example.com", "secret123").andExpect(status().isCreated());
            String token = loginAndGetToken("john@example.com", "secret123");

            // Flip the first character of the signature (the last one may only hold padding bits)
            String[] parts = token.split("\\.");
            char first = parts[2].charAt(0);
            String tampered = parts[0] + "." + parts[1] + "." + (first == 'A' ? 'B' : 'A') + parts[2].substring(1);

            getMe("Bearer " + tampered)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should reject a token signed with another secret")
        void shouldRejectForeignToken() throws Exception {
            User saved = userRepository.save(
                    new User(null, "john", "john@example.com", hashPort.hash("secret123")));
            String foreignToken = new JwtTokenAdapter("another-secret-key-that-is-also-32-bytes-long!!", 60_000)
                    .generateToken(saved);

            getMe("Bearer " + foreignToken)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should reject garbage tokens and non-Bearer schemes")
        void shouldRejectMalformedAuthorizationHeaders() throws Exception {
            getMe("Bearer not-a-jwt").andExpect(status().isUnauthorized());
            getMe("Bearer ").andExpect(status().isUnauthorized());
            getMe("Basic am9objpzZWNyZXQxMjM=").andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Unknown routes also require a token")
        void shouldProtectEveryOtherRoute() throws Exception {
            mockMvc.perform(get("/api/accounts"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
