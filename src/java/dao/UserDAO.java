package dao;

import java.sql.*;
import java.util.*;
import model.User;

public class UserDAO extends DBContext {

    // ✅ Login với MD5, kiểm tra status
    public User login(String username, String password) {
        String hashedPassword = md5(password);
        String sql = "SELECT * FROM Users WHERE username=? AND password=? AND status=1";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, hashedPassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Kiểm tra username đã tồn tại chưa
    public boolean isUsernameExist(String username) {
        String sql = "SELECT id FROM Users WHERE username=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Kiểm tra email đã tồn tại chưa
    public boolean isEmailExist(String email) {
        String sql = "SELECT id FROM Users WHERE email=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Register với password MD5
    public boolean register(User user) {
        String sql = "INSERT INTO Users(username,password,fullname,email,phone,role,status) VALUES(?,?,?,?,?,?,1)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername().trim());
            ps.setString(2, md5(user.getPassword())); // hash MD5
            ps.setString(3, user.getFullname());
            ps.setString(4, user.getEmail().trim());
            ps.setString(5, user.getPhone());
            ps.setString(6, "user");
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Kiểm tra username + email để reset password
    public User checkUserByUsernameAndEmail(String username, String email) {
        String sql = "SELECT * FROM Users WHERE username=? AND email=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, email.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Update password với MD5
    public void updatePassword(int id, String newPassword) {
        String sql = "UPDATE Users SET password=? WHERE id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, md5(newPassword));
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Đổi mật khẩu có xác thực mật khẩu cũ
    public boolean changePassword(int id, String oldPassword, String newPassword) {
        String sql = "SELECT id FROM Users WHERE id=? AND password=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, md5(oldPassword));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                updatePassword(id, newPassword);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private User extractUser(ResultSet rs) throws Exception {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("fullname"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("avatar"),
                rs.getString("role"),
                rs.getBoolean("status"),
                rs.getTimestamp("createdDate")
        );
    }
}