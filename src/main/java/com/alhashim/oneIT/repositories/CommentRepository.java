package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
