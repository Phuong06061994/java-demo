package io.bootify.my_app.repos;

import io.bootify.my_app.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TaskRepository extends JpaRepository<Task, Long> {
}
