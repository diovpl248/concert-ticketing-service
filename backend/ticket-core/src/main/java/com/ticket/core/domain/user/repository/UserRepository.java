package com.ticket.core.domain.user.repository;

import com.ticket.core.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
