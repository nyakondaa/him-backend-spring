package Him.admin.Services;
import Him.admin.DTO.Users.UserRequestDTO;
import Him.admin.Exceptions.ResourceAlreadyExistsException;
import Him.admin.Exceptions.ResourceNotFoundException;
import Him.admin.Models.Branch;
import Him.admin.Models.User;
import Him.admin.Models.Role;
import Him.admin.Repositories.BranchRepository;
import Him.admin.Repositories.UserRepository;
import Him.admin.Repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService { // Implement UserDetailsService

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final BranchRepository branchRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // ✅ Implement UserDetailsService method
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // Find user by username (returns Optional<User>)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Create a new user
    public User createUser(String username, String rawPassword, String email, String firstName, String lastName, Long branchID,Set<String> roleNames) {

        Set<Role> roles = roleRepository.findAll()
                .stream()
                .filter(r -> roleNames.stream()
                        .anyMatch(name -> name.equalsIgnoreCase(r.getName())))
                .collect(Collectors.toSet());

        Branch userBranch = branchRepository.findById(branchID)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", branchID));

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword)) // hash the password
                .firstName(firstName)
                .lastName(lastName)
                .branch(userBranch)
                .email(email)
                .roles(roles)
                .locked(false)
                .failedLoginAttempts(0)
                .build();

        if (userRepository.existsByUsername((user.getUsername()))) {
            throw new ResourceAlreadyExistsException("User", "username", user.getUsername());
        }

        if (userRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException("User", "email", email);
        }

        User savedUser = userRepository.save(user);

        logger.info("✅ User '{}' created with ID {}", savedUser.getUsername(), savedUser.getId());

        return savedUser;
    }

    // Lock a user
    public void lockUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLocked(true);
            userRepository.save(user);
        });
    }

    // Unlock a user
    public void unlockUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLocked(false);
            user.setFailedLoginAttempts(0); // reset attempts
            userRepository.save(user);
        });
    }

    // Increment failed login attempts
    public void incrementFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= 3) {
            user.setLocked(true);
        }
        userRepository.save(user);
    }

    public boolean authenticate(String username, String rawPassword) {
        Optional<User> optionalUser = findByUsername(username);
        if (optionalUser.isEmpty()) return false;

        User user = optionalUser.get();

        // Check if user is locked
        if (user.isLocked()) return false;

        // Check password
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            user.setFailedLoginAttempts(0); // reset failed attempts on success
            userRepository.save(user);
            return true;
        } else {
            incrementFailedLogin(user);
            return false;
        }
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Set<User> findAll() {
        return new HashSet<>(userRepository.findAll());
    }

    // ✅ Update user
    public User updateUser(Long userId, UserRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Update basic fields
        user.setUsername(dto.getUsername() != null ? dto.getUsername() : user.getUsername());
        user.setEmail(dto.getEmail() != null ? dto.getEmail() : user.getEmail());
        user.setFirstName(dto.getFirstName() != null ? dto.getFirstName() : user.getFirstName());
        user.setLastName(dto.getLastName() != null ? dto.getLastName() : user.getLastName());

        // Update password if provided
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Update roles if provided
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            Set<Role> roles = roleRepository.findAll()
                    .stream()
                    .filter(r -> dto.getRoles().contains(r.getName()))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        userRepository.delete(user);
    }
}