package net.ukr.kondrashev.repositories;

import net.ukr.kondrashev.entities.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomUserRepository extends JpaRepository<CustomUser, Long> {
    @Query("SELECT u FROM CustomUser u where u.login = :login")
    CustomUser findByLogin(@Param("login") String login);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM CustomUser u WHERE u.login = :login")
    boolean existsByLogin(@Param("login") String login);

    @Query("SELECT u FROM CustomUser u WHERE LOWER(u.role) LIKE LOWER(CONCAT('', :pattern, ''))")
    List<CustomUser> findByUser(@Param("pattern") String pattern);
}