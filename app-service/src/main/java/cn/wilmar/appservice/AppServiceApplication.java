package cn.wilmar.appservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;


@SpringBootApplication
public class AppServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppServiceApplication.class, args);
    }
}

@Entity
class User {
    @Id
    @GeneratedValue
    Long id;
    String name;
    LocalDate created;

    public User() {
    }

    public User(String name) {
        this.name = name;
        this.created = LocalDate.now();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", created=" + created +
                '}';
    }

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDate getCreated() {
        return created;
    }

    public User setCreated(LocalDate created) {
        this.created = created;
        return this;
    }
}

@RepositoryRestResource
interface UserRepository extends JpaRepository<User, Long> {
    public List<User> findByName(String name);
}

@Component
class UserDataCLR implements CommandLineRunner {
    private final UserRepository userRepository;

    @Autowired
    public UserDataCLR(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... strings) throws Exception {
        Stream.of("Gary", "Will", "James", "Jon").forEach(name -> userRepository.save(new User(name)));
        userRepository.findAll().forEach(System.out::println);
    }
}