package spring.security.ldap.demo.util;

import org.springframework.core.env.Environment;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.naming.directory.SearchControls;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class LdapAuthenticationProvider implements AuthenticationProvider {
    private Environment environment;
    private final PasswordEncoder passwordEncoder;

    public LdapAuthenticationProvider(Environment environment) {
        this.environment = environment;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    private LdapContextSource contextSource;
    private LdapTemplate ldapTemplate;

    private void initContext() {
        contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://localhost:8389/dc=springframework,dc=org");
        contextSource.setAnonymousReadOnly(true);
        contextSource.setUserDn("uid={0},ou=people");
        contextSource.afterPropertiesSet();

        ldapTemplate = new LdapTemplate(contextSource);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        initContext();
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Search for the user entry
        Filter filter = new EqualsFilter("uid", username);
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        List<UserDetails> users = ldapTemplate.search(
                LdapUtils.emptyLdapName(),
                filter.encode(),
                searchControls,
                (AttributesMapper<UserDetails>) attrs -> {
                    String uid = (String) attrs.get("uid").get();
                    byte[] passwordBytes = (byte[]) attrs.get("userPassword").get();
                    String userPassword = new String(passwordBytes, StandardCharsets.UTF_8);
                    return new User(uid, userPassword, new ArrayList<>());
                }
        );

        if (!users.isEmpty()) {
            UserDetails userDetails = users.get(0);
            if (passwordEncoder.matches(password, userDetails.getPassword())) {
                return new UsernamePasswordAuthenticationToken(userDetails, password, new ArrayList<>());
            }
        }

        throw new AuthenticationException("Authentication failed for user: " + username) {};
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
