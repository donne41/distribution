package example.service1.services;

import example.service1.users.UserEntity;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface UserRepository extends ListCrudRepository<UserEntity, Long>{

    UserEntity findByUserName(String username);

    long count();

    UserEntity findFirstById(Long i);

    List<UserEntity> findAll();

    boolean existsByUserName(String userName);

}
