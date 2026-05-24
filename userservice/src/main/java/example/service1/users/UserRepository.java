package example.service1.users;

import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserRepository extends ListCrudRepository<UserEntity, Long>{

    UserEntity findByUserName(String username);

    long count();

    UserEntity findFirstById(int i);

    @NativeQuery("select * from user_db.public.user_entity")
    List<UserEntity> getAllUsers();

    boolean existsByUserName(String userName);
}
