package com.clone.instagram.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@ToString(of = {"id", "email", "phone", "name", "title"}, exclude = {"postList"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;
    private String phone;
    private String name;
    private String title;
    private String website;
    private String profileImgUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties({"user"})
    private List<Post> postList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties({"user"})
    private List<Comment> commentList = new ArrayList<>();

    @Builder
    public User(String email, String password, String phone, String name, String title, String website, String profileImgUrl) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.title = title;
        this.website = website;
        this.profileImgUrl = profileImgUrl;
    }

    public void update(String password, String phone, String name, String title, String website) {
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.title = title;
        this.website = website;
    }

    public void updateProfileUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

}
