package com.moc.vocabularywebapp.service.user;

public interface SecurityService {
    public void save(String username, String password);
    public void logout();

}
