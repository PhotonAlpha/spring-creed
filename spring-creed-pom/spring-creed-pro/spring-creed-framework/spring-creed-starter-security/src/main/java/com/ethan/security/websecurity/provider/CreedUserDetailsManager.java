package com.ethan.security.websecurity.provider;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.security.websecurity.entity.CreedAuthorities;
import com.ethan.security.websecurity.entity.CreedGroupAuthorities;
import com.ethan.security.websecurity.entity.CreedGroupMembers;
import com.ethan.security.websecurity.entity.CreedGroups;
import com.ethan.security.websecurity.entity.CreedUser;
import com.ethan.security.websecurity.repository.CreedAuthorityRepository;
import com.ethan.security.websecurity.repository.CreedGroupsAuthoritiesRepository;
import com.ethan.security.websecurity.repository.CreedGroupsMembersRepository;
import com.ethan.security.websecurity.repository.CreedGroupsRepository;
import com.ethan.security.websecurity.repository.CreedUserRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Deprecated
public class CreedUserDetailsManager implements UserDetailsManager, GroupManager, MessageSourceAware, InitializingBean {
    private final CreedAuthorityRepository authorityRepository;
    private final CreedUserRepository creedUserRepository;
    private final CreedGroupsAuthoritiesRepository groupsAuthoritiesRepository;
    private final CreedGroupsMembersRepository groupsMembersRepository;
    private final CreedGroupsRepository groupsRepository;

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private String rolePrefix = "";

    private boolean usernameBasedPrimaryKey = true;
    private UserCache userCache = new NullUserCache();

    private boolean enableAuthorities = true;
    private boolean enableGroups;

    private AuthenticationManager authenticationManager;
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();


    public CreedUserDetailsManager(CreedAuthorityRepository authorityRepository,
                                   CreedUserRepository creedUserRepository,
                                   CreedGroupsAuthoritiesRepository groupsAuthoritiesRepository,
                                   CreedGroupsMembersRepository groupsMembersRepository,
                                   CreedGroupsRepository groupsRepository) {
        this.authorityRepository = authorityRepository;
        this.creedUserRepository = creedUserRepository;
        this.groupsAuthoritiesRepository = groupsAuthoritiesRepository;
        this.groupsMembersRepository = groupsMembersRepository;
        this.groupsRepository = groupsRepository;
    }

    @Override
    public List<String> findAllGroups() {
        return groupsRepository.findAllByEnabled()
                .stream().map(CreedGroups::getGroupname)
                .toList();
    }

    @Override
    public List<String> findUsersInGroup(String groupName) {
        return groupsMembersRepository.findUsersInGroup(groupName)
                .stream().map(CreedGroupMembers::getUsername)
                .toList();
    }

    @Override
    public void createGroup(String groupName, List<GrantedAuthority> authorities) {
        Assert.hasText(groupName, "groupName should have text");
        Assert.notNull(authorities, "authorities cannot be null");
        groupsRepository.save(toGroupEntity(groupName, authorities));
    }

    private CreedGroups toGroupEntity(String groupName, List<GrantedAuthority> authorities) {
        Assert.hasText(groupName, "groupName should have text");
        Assert.notNull(authorities, "authorities cannot be null");
        log.debug("Creating new group '{}' with authorities {}", groupName,
                AuthorityUtils.authorityListToSet(authorities));

        CreedGroups group = new CreedGroups();
        group.setGroupname(groupName);

        List<CreedGroupAuthorities> groupAuthorities = authorities.stream()
                .map(a -> new CreedGroupAuthorities(a.getAuthority(), group))
                .toList();

        group.setAuthorities(groupAuthorities);

        return group;
    }

    @Override
    public void deleteGroup(String groupName) {
        log.debug("Deleting group '{}'", groupName);
        Assert.hasText(groupName, "groupName should have text");
        Optional<CreedGroups> groupsOptional = groupsRepository.findByGroupname(groupName);
        groupsOptional.ifPresent(groupsRepository::delete);
    }

    @Override
    public void renameGroup(String oldName, String newName) {
        log.debug("Changing group name from '{}' to '{}'", oldName, newName);
        Assert.hasText(oldName, "oldName should have text");
        Assert.hasText(newName, "newName should have text");
        Optional<CreedGroups> groupsOptional = groupsRepository.findByGroupname(oldName);
        if (groupsOptional.isPresent()) {
            CreedGroups creedGroups = groupsOptional.get();
            creedGroups.setGroupname(newName);
            groupsRepository.save(creedGroups);
        }
    }

    @Override
    public void addUserToGroup(String username, String group) {
        //TODO
    }

    @Override
    public void removeUserFromGroup(String username, String groupName) {
        //TODO
    }

    @Override
    public List<GrantedAuthority> findGroupAuthorities(String groupName) {
        //TODO
        return null;
    }

    @Override
    public void addGroupAuthority(String groupName, GrantedAuthority authority) {
        //TODO
    }

    @Override
    public void removeGroupAuthority(String groupName, GrantedAuthority authority) {
        //TODO
    }

    @Override
    @Transactional
    public void createUser(UserDetails user) {
        validateUserDetails(user);
        CreedUser consumer = toEntity(user);

        creedUserRepository.save(consumer);
        if (getEnableAuthorities()) {
            insertUserAuthorities(user, consumer);
        }
    }

    private CreedUser toEntity(UserDetails user) {
        Assert.notNull(user, "UserDetails can not be null");
        CreedUser creedUser = new CreedUser();
        creedUser.setUsername(user.getUsername());
        creedUser.setPassword(user.getPassword());
        creedUser.setEnabled(CommonStatusEnum.convert(user.isEnabled()));
        // NOTE: acc_locked, acc_expired and creds_expired are also to be inserted
        creedUser.setAccNonLocked(CommonStatusEnum.convert(!user.isAccountNonLocked()));
        creedUser.setAccNonExpired(CommonStatusEnum.convert(!user.isAccountNonExpired()));
        creedUser.setCredentialsNonExpired(CommonStatusEnum.convert(!user.isCredentialsNonExpired()));
        return creedUser;
    }

    private void insertUserAuthorities(UserDetails user, CreedUser creedUser) {
        var authorities = new HashSet<CreedAuthorities>();
        var creedUserAuthorities = new HashSet<CreedAuthorities>();
        for (GrantedAuthority auth : user.getAuthorities()) {
            Optional<CreedAuthorities> creedAuthoritiesOp = authorityRepository.findByAuthority(auth.getAuthority());
            if (creedAuthoritiesOp.isEmpty()) {
                CreedAuthorities creedAuthorities = new CreedAuthorities();
                creedAuthorities.setAuthority(auth.getAuthority());
                authorities.add(creedAuthorities);

                creedUserAuthorities.add(creedAuthorities);
            } else {
                creedUserAuthorities.add(creedAuthoritiesOp.get());
            }
        }
        authorityRepository.saveAll(authorities);
        creedUser.setAuthorities(creedUserAuthorities);
    }

    @Override
    @Transactional
    //update users set password = ?, enabled = ? where username = ?
    public void updateUser(UserDetails user) {
        validateUserDetails(user);

        Optional<CreedUser> consumerOptional = creedUserRepository.findByUsername(user.getUsername());
        if (consumerOptional.isPresent()) {
            CreedUser creedUser = consumerOptional.get();
            if (getEnableAuthorities()) {
                //delete Authorities and insert
                deleteUserAuthorities(creedUser);
                insertUserAuthorities(user, creedUser);
            }

            this.userCache.removeUserFromCache(user.getUsername());
        } else {
            log.warn("unknow user:{}", user.getUsername());
        }
    }

    private void deleteUserAuthorities(CreedUser creedUser) {
        Assert.notNull(creedUser, "CreedConsumer can not be null");
        Optional<CreedUser> userOptional = creedUserRepository.findById(creedUser.getId());
        Set<CreedAuthorities> creedAuthorities = userOptional.map(CreedUser::getAuthorities).orElse(Collections.emptySet());
        authorityRepository.deleteAll(creedAuthorities);
    }

    @Override
    @Transactional
    public void deleteUser(String username) {
        Optional<CreedUser> consumerOptional = creedUserRepository.findByUsername(username);
        if (consumerOptional.isPresent()) {
            CreedUser creedUser = consumerOptional.get();
            if (getEnableAuthorities()) {
                deleteUserAuthorities(creedUser);
            }
            creedUserRepository.deleteByUsername(username);
            this.userCache.removeUserFromCache(username);
        } else {
            log.warn("unknow user:{}", username);
        }
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();
        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context " + "for current user.");
        }
        String username = currentUser.getName();

        Optional<CreedUser> consumerOptional = creedUserRepository.findByUsername(username);
        if (consumerOptional.isPresent()) {
            CreedUser creedUser = consumerOptional.get();
            // If an authentication manager has been set, re-authenticate the user with the
            // supplied password.
            if (this.authenticationManager != null) {
                log.debug("Reauthenticating user '{}' for password change request.", username);
                this.authenticationManager
                        .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));
            }
            else {
                log.debug("No authentication manager set. Password won't be re-checked.");
            }
            log.debug("Changing password for user '" + username + "'");

            creedUser.setPassword(newPassword);

            Authentication authentication = createNewAuthentication(currentUser, newPassword);
            SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authentication);
            this.securityContextHolderStrategy.setContext(context);

            creedUserRepository.save(creedUser);

            this.userCache.removeUserFromCache(username);
        } else {
            log.warn("unknow user:{}", username);
        }
    }

    protected Authentication createNewAuthentication(Authentication currentAuth, String newPassword) {
        UserDetails user = loadUserByUsername(currentAuth.getName());
        UsernamePasswordAuthenticationToken newAuthentication = UsernamePasswordAuthenticationToken.authenticated(user,
                null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }

    @Override
    public boolean userExists(String username) {
        Optional<CreedUser> consumerOptional = null;
        try {
            consumerOptional = creedUserRepository.findByUsername(username);
            return consumerOptional.isPresent();
        } catch (Exception e) {
            throw new IncorrectResultSizeDataAccessException("More than one user found with name '" + username + "'",
                    1);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // List<UserDetails> users = loadUsersByUsername(username);
        Optional<CreedUser> consumerOptional = creedUserRepository.findByUsername(username);
        if (consumerOptional.isEmpty()) {
            log.debug("Query returned no results for user '" + username + "'");
            throw new UsernameNotFoundException(this.messages.getMessage("JdbcDaoImpl.notFound",
                    new Object[] { username }, "Username {0} not found"));
        }

        UserDetails user = loadUsersByUsername(consumerOptional.get()); // contains no GrantedAuthority[]
        Set<GrantedAuthority> dbAuthsSet = new HashSet<>();
        if (this.enableAuthorities) {
            dbAuthsSet.addAll(loadUserAuthorities(consumerOptional.get()));
        }
        if (this.enableGroups) {
            dbAuthsSet.addAll(loadGroupAuthorities(consumerOptional.get()));
        }
        List<GrantedAuthority> dbAuths = new ArrayList<>(dbAuthsSet);
        addCustomAuthorities(user.getUsername(), dbAuths);
        if (dbAuths.size() == 0) {
            log.debug("User '" + username + "' has no authorities and will be treated as 'not found'");
            throw new UsernameNotFoundException(this.messages.getMessage("JdbcDaoImpl.noAuthority",
                    new Object[] { username }, "User {0} has no GrantedAuthority"));
        }
        return createUserDetails(username, user, dbAuths);
    }

    /**
     * Can be overridden to customize the creation of the final UserDetailsObject which is
     * returned by the <tt>loadUserByUsername</tt> method.
     * @param username the name originally passed to loadUserByUsername
     * @param userFromUserQuery the object returned from the execution of the
     * @param combinedAuthorities the combined array of authorities from all the authority
     * loading queries.
     * @return the final UserDetails which should be used in the system.
     */
    private UserDetails createUserDetails(String username, UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {

        String returnUsername = userFromUserQuery.getUsername();
        if (!this.usernameBasedPrimaryKey) {
            returnUsername = username;
        }
        return new User(returnUsername, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(),
                userFromUserQuery.isAccountNonExpired(), userFromUserQuery.isCredentialsNonExpired(),
                userFromUserQuery.isAccountNonLocked(), combinedAuthorities);
    }

    /**
     * Allows subclasses to add their own granted authorities to the list to be returned
     * in the <tt>UserDetails</tt>.
     * @param username the username, for use by finder methods
     * @param authorities the current granted authorities, as populated from the
     * <code>authoritiesByUsername</code> mapping
     */
    protected void addCustomAuthorities(String username, List<GrantedAuthority> authorities) {
    }

    private List<GrantedAuthority> loadGroupAuthorities(CreedUser consumer) {
        // groupsMembersRepository.findUsersInGroup()
        return Collections.emptyList();
    }

    private List<GrantedAuthority> loadUserAuthorities(CreedUser consumer) {
        return consumer.getAuthorities()
                .stream()
                .map(CreedAuthorities::getAuthority)
                .map(this::grantedAuthority)
                .toList();
    }

    private GrantedAuthority grantedAuthority(String s) {
        return new SimpleGrantedAuthority(s);
    }

    protected UserDetails loadUsersByUsername(CreedUser creedUser) {
        Assert.notNull(creedUser, "CreedConsumer can not be null");
        return new User(creedUser.getUsername(), creedUser.getPassword(),
                creedUser.getEnabled().enabled(),
                creedUser.getAccNonExpired().enabled(),
                creedUser.getCredentialsNonExpired().enabled(),
                creedUser.getAccNonLocked().enabled(),
                AuthorityUtils.NO_AUTHORITIES);
    }


    private void validateUserDetails(UserDetails user) {
        Assert.hasText(user.getUsername(), "Username may not be empty or null");
        validateAuthorities(user.getAuthorities());
    }

    private void validateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Authorities list must not be null");
        for (GrantedAuthority authority : authorities) {
            Assert.notNull(authority, "Authorities list contains a null entry");
            Assert.hasText(authority.getAuthority(), "getAuthority() method must return a non-empty string");
        }
    }

    /**
     * Optionally sets the UserCache if one is in use in the application. This allows the
     * user to be removed from the cache after updates have taken place to avoid stale
     * data.
     * @param userCache the cache used by the AuthenticationManager.
     */
    public void setUserCache(UserCache userCache) {
        Assert.notNull(userCache, "userCache cannot be null");
        this.userCache = userCache;
    }

    protected boolean getEnableAuthorities() {
        return this.enableAuthorities;
    }

    /**
     * Enables loading of authorities (roles) from the authorities table. Defaults to true
     */
    public void setEnableAuthorities(boolean enableAuthorities) {
        this.enableAuthorities = enableAuthorities;
    }

    protected boolean getEnableGroups() {
        return this.enableGroups;
    }

    /**
     * Enables support for group authorities. Defaults to false
     * @param enableGroups
     */
    public void setEnableGroups(boolean enableGroups) {
        this.enableGroups = enableGroups;
    }


    @Override
    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource cannot be null");
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.authenticationManager == null) {
            log.info(
                    "No authentication manager set. Reauthentication of users when changing passwords will not be performed.");
        }
    }

    @Resource
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
