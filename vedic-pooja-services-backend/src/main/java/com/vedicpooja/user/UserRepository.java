package com.vedicpooja.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository&lt;User, Long&gt; {
    Optional&lt;User&gt; findByEmail(String email);
    Optional&lt;User&gt; findByPhone(String phone);
}