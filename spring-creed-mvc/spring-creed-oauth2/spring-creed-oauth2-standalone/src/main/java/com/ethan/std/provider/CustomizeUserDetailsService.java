package com.ethan.std.provider;

import com.ethan.std.provisioning.UserProfile;
import com.ethan.std.provisioning.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Optional;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/2/2022 4:37 PM
 */
public class CustomizeUserDetailsService implements UserDetailsManager {
    private static final Logger log = LoggerFactory.getLogger(CustomizeUserDetailsService.class);
    private UserProfileRepository profileRepository;

    private AuthenticationManager authenticationManager;

    @Autowired
    public void setProfileRepository(UserProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void createUser(UserDetails user) {
        if (user instanceof UserProfile) {
            profileRepository.save((UserProfile) user);
        } else {
            log.error("WARNING:UserDetails[{}] type not matched!", user.getClass().getSimpleName());
        }
    }

    @Override
    public void updateUser(UserDetails user) {
        if (user instanceof UserProfile) {
            profileRepository.save((UserProfile) user);
        } else {
            log.error("WARNING:UserDetails[{}] type not matched!", user.getClass().getSimpleName());
        }
    }

    @Override
    public void deleteUser(String username) {
        profileRepository.deleteByName(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context " + "for current user.");
        }
        String username = currentUser.getName();
        // If an authentication manager has been set, re-authenticate the user with the
        // supplied password.
        if (this.authenticationManager != null) {
            log.debug("{}", LogMessage.format("Reauthenticating user '%s' for password change request.", username));
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
        }
        else {
            log.debug("No authentication manager set. Password won't be re-checked.");
        }
        log.debug("Changing password for user '" + username + "'");
        // getJdbcTemplate().update(this.changePasswordSql, newPassword, username);
        profileRepository.updatePassword(username, newPassword);
        Authentication authentication = createNewAuthentication(currentUser, newPassword);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    protected Authentication createNewAuthentication(Authentication currentAuth, String newPassword) {
        UserDetails user = loadUserByUsername(currentAuth.getName());
        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(user, null,
                user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }

    @Override
    public boolean userExists(String username) {
        Optional<UserProfile> profileOptional = profileRepository.findByUsername(username);
        return profileOptional.isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserProfile> profileOptional = profileRepository.findByUsername(username);
        if (profileOptional.isPresent()) {
            return profileOptional.get();
        }
        return null;
    }
}
