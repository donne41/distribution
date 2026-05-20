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
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;


    public UserEntity(String name, String username, String password, List<String> roles) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.authorities = setAuthoritesList(roles);
    }
    public UserEntity(String username, String password){
        this("", username, password, List.of("user"));
    }

    public UserEntity(){
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
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
