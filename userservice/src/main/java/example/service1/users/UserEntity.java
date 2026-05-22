package example.service1.users;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
@Getter
@Setter
@Entity
public class UserEntity implements UserDetails{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String name;
    private String userName;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;


    public UserEntity(String name, String username, String password, List<String> roles) {
        this.name = name;
        this.userName = username;
        this.password = password;
        this.authorities = setAuthoritesList(roles);
    }
    public UserEntity(String username, String password){
        this("", username, password, List.of("user"));
    }

    public UserEntity(){
    }

    @Override
    public String getUsername() {
        return userName;
    }
    @Override
    public String getPassword(){
        return password;
    }
    public String getName(){ return name;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public List<String> getAuthAsList(){
        return authorities.stream().map(
                role -> role.toString())
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                .toList();
    }

    public void setAuthorities(List<String> roles) {
        this.authorities = setAuthoritesList(roles);
    }

    public List<SimpleGrantedAuthority> setAuthoritesList(List<String> roles) {
        return roles.stream().map(
                        r -> r.startsWith("ROLE_") ? new SimpleGrantedAuthority(r.toUpperCase()) : new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                .toList();
    }
}
