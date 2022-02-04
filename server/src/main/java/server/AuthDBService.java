package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthDBService implements AuthService {
    private class UserData {
        String login;
        String password;
        String nickname;

        public UserData(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    private List<UserData> users;
    private Connection authConnection;
    private Statement authStmt;

    public AuthDBService(Connection connection, Statement stmt) {
        this.users = new ArrayList<>();
        this.authConnection = connection;
        this.authStmt = stmt;

        try (ResultSet rs = authStmt.executeQuery("Select * FROM users;")) {
            while (rs.next()) {
                users.add(new UserData(rs.getString("login"), rs.getString("passwd"), rs.getString("nickname")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (UserData user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.nickname;
            }
        }
        return null;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        for (UserData user : users) {
            if (user.login.equals(login) || user.nickname.equals(nickname)) {
                return false;
            }
        }
        users.add(new UserData(login, password, nickname));
        try (PreparedStatement prepInsert = authConnection.prepareStatement("INSERT INTO users (nickname,login,passwd) VALUES (?,?,?);")) {
            prepInsert.setString(1, nickname);
            prepInsert.setString(2,login);
            prepInsert.setString(3,password);
            prepInsert.addBatch();
            prepInsert.executeBatch();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean changeNickName(String nickname, String login) {
        for (UserData user: users) {
            if (user.login.equals(login) && !user.nickname.equals(nickname)) {
                try (PreparedStatement prepUpdate = authConnection.prepareStatement("UPDATE users SET nickname = ? WHERE login = ?")) {
                    prepUpdate.setString(1, nickname);
                    prepUpdate.setString(2, login);
                    prepUpdate.addBatch();
                    prepUpdate.executeBatch();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return false;
    }
}
