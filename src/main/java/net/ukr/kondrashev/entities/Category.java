package net.ukr.kondrashev.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Categories1")
@NoArgsConstructor
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue
    private long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "custom_user_id")
    private CustomUser customUser;

    private String name;
    private String userName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Word> words;

    public Category(CustomUser customUser, String name, String userName) {
        this.customUser = customUser;
        this.name = name;
        this.userName = userName;
    }
}