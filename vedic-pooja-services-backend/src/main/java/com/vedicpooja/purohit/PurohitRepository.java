package com.vedicpooja.purohit;

import com.vedicpooja.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurohitRepository extends JpaRepository&lt;Purohit, Long&gt; {
    Optional&lt;Purohit&gt; findByUser(User user);
}