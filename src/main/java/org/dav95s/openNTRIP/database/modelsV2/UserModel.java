package org.dav95s.openNTRIP.database.modelsV2;

public class UserModel {
    public final int id;
    public final String username;
    public final String password;
    public final String email;

    public UserModel(int id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
