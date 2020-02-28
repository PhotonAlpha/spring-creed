package auth.repository;


import com.ethan.auth.model.User;
import com.ethan.auth.repository.base.BaseRepository;

public interface UserRepository extends BaseRepository<User> {

    User findUserByAccount(String account);
}
