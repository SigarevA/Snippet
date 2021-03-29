package ru.vsu.cs.Crocodile.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

/**
 * @author Sigarev Aleksei
 */
@Document(collection = "User")
public class User implements UserDetails {

    @Id
    private String id;
    private String name;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<String> likedSnippets;
    private Collection<String> authors;
    private Collection<String> followers;
    @JsonIgnore
    private Collection<GrantedAuthority> grantedAuthorities;
    private String tapeType;
    private String imagePath;

    public User() {
    }

    public User(String id, String name, String email, String password, Collection<String> likedSnippets, Collection<String> authors, Collection<String> followers, Collection<GrantedAuthority> grantedAuthorities, String tapeType, String imagePath) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.likedSnippets = likedSnippets;
        this.authors = authors;
        this.followers = followers;
        this.grantedAuthorities = grantedAuthorities;
        this.tapeType = tapeType;
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGrantedAuthorities(Collection<GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTapeType() {
        return tapeType;
    }

    public void setTapeType(String tapeType) {
        this.tapeType = tapeType;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return this.email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<String> getLikedSnippets() {
        return likedSnippets;
    }

    public void setLikedSnippets(Collection<String> likedSnippets) {
        this.likedSnippets = likedSnippets;
    }

    public Collection<String> getAuthors() {
        return authors;
    }

    public void setAuthors(Collection<String> authors) {
        this.authors = authors;
    }

    public Collection<String> getFollowers() {
        return followers;
    }

    public void setFollowers(Collection<String> followers) {
        this.followers = followers;
    }

    @Override
    public String toString() {
        return "User{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", likedSnippets=" + likedSnippets +
            ", authors=" + authors +
            ", followers=" + followers +
            ", grantedAuthorities=" + grantedAuthorities +
            ", tapeType='" + tapeType + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}