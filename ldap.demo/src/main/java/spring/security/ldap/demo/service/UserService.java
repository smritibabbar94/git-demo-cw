package spring.security.ldap.demo.service;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;
import spring.security.ldap.demo.vo.User;

import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import java.util.List;

@Service
public class UserService {
    private final LdapTemplate ldapTemplate;

    public UserService(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public List<User> getAllUsers() {
        return ldapTemplate.search(
                "ou=people,dc=springframework,dc=org",
                "(objectclass=person)",
                new AbstractContextMapper<User>() {
                    protected User doMapFromContext(DirContextOperations ctx) {
                        User user = new User();
                        user.setUid(ctx.getStringAttribute("uid"));
                        user.setCn(ctx.getStringAttribute("cn"));
                        user.setSn(ctx.getStringAttribute("sn"));
                        return user;
                    }
                }
        );
    }

    public void createUser(User user) {
        Name dn = LdapNameBuilder.newInstance()
                .add("dc", "org")
                .add("dc", "springframework")
                .add("ou", "people")
                .add("uid", user.getUid())
                .build();
        Attributes attributes = new BasicAttributes();
        attributes.put("objectclass", "inetOrgPerson");
        attributes.put("cn", user.getCn());
        attributes.put("sn", user.getSn());
        ldapTemplate.bind(dn, null, attributes);
    }

    public User getUser(String uid) {
        Name dn = LdapNameBuilder.newInstance()
                .add("dc", "org")
                .add("dc", "springframework")
                .add("ou", "people")
                .add("uid", uid)
                .build();
        return ldapTemplate.lookup(dn, new AbstractContextMapper<User>() {
            protected User doMapFromContext(DirContextOperations ctx) {
                User user = new User();
                user.setUid(ctx.getStringAttribute("uid"));
                user.setCn(ctx.getStringAttribute("cn"));
                user.setSn(ctx.getStringAttribute("sn"));
                return user;
            }
        });
    }

    public void updateUser( String uid) {
        Name dn = LdapNameBuilder.newInstance()
                .add("dc", "org")
                .add("dc", "springframework")
                .add("ou", "people")
                .add("uid", uid)
                .build();
        DirContextOperations context = ldapTemplate.lookupContext(dn);
        context.setAttributeValues("objectclass", new String[] { "top", "person", "organizationalPerson", "inetOrgPerson" });
        context.setAttributeValue("cn", "Lord");
        context.setAttributeValue("sn", "Bobby");
        ldapTemplate.modifyAttributes(context);
    }

    public void deleteUser(String uid) {
        Name dn = LdapNameBuilder.newInstance()
                .add("dc", "org")
                .add("dc", "springframework")
                .add("ou", "people")
                .add("uid", uid)
                .build();
        ldapTemplate.unbind(dn);
    }
}
