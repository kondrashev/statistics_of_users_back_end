package net.ukr.kondrashev.repositories;

import net.ukr.kondrashev.entities.Category;
import net.ukr.kondrashev.entities.CustomUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.customUser = :user")
    List<Category> findByUser(@Param("user") CustomUser customUser, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Category u WHERE u.name = :name and u.userName=:userName")
    boolean existsByName(@Param("name") String name, @Param("userName") String userName);

    @Query("SELECT u FROM Category u where u.name = :name")
    Category findByName(@Param("name") String name);
}