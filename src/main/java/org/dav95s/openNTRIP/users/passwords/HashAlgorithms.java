package org.dav95s.openNTRIP.users.passwords;

public enum HashAlgorithms {
    None(new None()), BCrypt(new BCrypt()), SHA256(new SHA256());

    public PasswordHandler passwordHandler;

    HashAlgorithms(PasswordHandler passwordHandler) {
        this.passwordHandler = passwordHandler;
    }
}
