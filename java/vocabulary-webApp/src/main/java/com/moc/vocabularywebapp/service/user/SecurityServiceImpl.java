package com.moc.vocabularywebapp.service.user;

import com.moc.vocabularywebapp.constant.MessageKeys;
import com.moc.vocabularywebapp.constant.ResourceBundleNames;
import com.moc.vocabularywebapp.constant.RouteKeys;
import com.moc.vocabularywebapp.model.User;
import com.moc.vocabularywebapp.repository.SecurityRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


@Service
public class SecurityServiceImpl implements SecurityService, UserDetailsService {

    private final SecurityRepository securityRepository;
    private final BCryptPasswordEncoder encoder;
    private Locale locale;
    private ResourceBundle route;
    private ResourceBundle message;

    @Autowired
    public SecurityServiceImpl(SecurityRepository securityRepository, BCryptPasswordEncoder encoder) {
        this.securityRepository = securityRepository;
        this.encoder = encoder;
        this.locale = UI.getCurrent().getLocale();
        this.route = ResourceBundle.getBundle(ResourceBundleNames.ROUTE_BUNDLE, locale);
        this.message = ResourceBundle.getBundle(ResourceBundleNames.MESSAGE_BUNDLE, locale);
    }

    @Override
    public void save(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        securityRepository.save(user);
    }

    @Override
    public void logout() {
        UI.getCurrent().getPage().setLocation(route.getString(RouteKeys.LOGIN_ROUTE));
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = securityRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(message.getString(MessageKeys.USERNAME_NOT_FOUND) + username);
        }
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

}
