package example.service1.users;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends ListCrudRepository<UserEntity, Long>{

    UserEntity findByUserName(String username);

    long count();
}
