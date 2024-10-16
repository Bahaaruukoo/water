package com.UserManagement.account.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.UserManagement.account.Entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

}