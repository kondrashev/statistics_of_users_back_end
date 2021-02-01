package net.ukr.kondrashev.repositories;

import net.ukr.kondrashev.entities.Category;
import net.ukr.kondrashev.entities.Word;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    @Query("SELECT c FROM Word c WHERE c.category = :category")
    List<Word> findByCategory(@Param("category") Category category, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Word u WHERE u.name = :name and u.userName=:userName")
    boolean existsByName(@Param("name") String name, @Param("userName") String userName);

    @Query("SELECT u FROM Word u where u.name = :name")
    Word findByName(@Param("name") String name);
}