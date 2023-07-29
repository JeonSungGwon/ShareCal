package Capstone.repository;

import com.example.Capstone.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    List<Member> findAllByEmailIn(List<String> emails);

    //Member findByUsername(String username);
    Member findByNickname(String Nickname);

    boolean existsByEmail(String email);
}