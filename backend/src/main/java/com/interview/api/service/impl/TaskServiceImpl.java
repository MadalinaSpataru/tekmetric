package com.interview.api.service.impl;

import com.interview.api.exception.RecordNotFoundException;
import com.interview.api.mapper.TaskMapper;
import com.interview.api.model.Task;
import com.interview.api.repository.TaskRepository;
import com.interview.api.service.TaskService;
import com.interview.api.service.UserService;
import com.interview.api.dto.TaskDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {


    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        taskMapper = new TaskMapper();
    }

    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        return taskMapper.toTaskDTO(taskRepository.save(taskMapper.toTask(taskDTO)));
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElse(null);

        if (null == task) {
            throw new RecordNotFoundException("Couldn't find task with id: " + id);
        }

        return taskMapper.toTaskDTO(task);
    }

    @Override
    public Page<TaskDTO> getTasks(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
        Pageable pageable = null;

        if (sortBy != null & sortDirection != null) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.fromString(sortDirection), sortBy);
        } else {
            pageable = PageRequest.of(pageNumber, pageSize);
        }

        Page<Task> users = taskRepository.findAll(pageable);

        return users.map(taskMapper::toTaskDTO);
    }

    @Override
    public List<TaskDTO> getTasksByTitle(String title) {
        return taskMapper.toTaskDTOs(taskRepository.findByTitleStartsWith(title));
    }

    @Override
    public TaskDTO updateTask(TaskDTO taskDTO) {
        if (!taskRepository.existsById(taskDTO.getId())) {
            throw new RecordNotFoundException("Update operation aborted. Couldn't find task with id: " + taskDTO.getId());
        }

        Task task = taskMapper.toTask(taskDTO);
        return taskMapper.toTaskDTO(taskRepository.save(task));
    }

    @Override
    public TaskDTO patchTask(TaskDTO taskDTO) {
        Task task = taskRepository.findById(taskDTO.getId()).orElse(null);

        if (null == task) {
            throw new RecordNotFoundException("Update operation aborted. Couldn't find task with id: " + taskDTO.getId());
        }

        return taskMapper.toTaskDTO(taskMapper.copyfrom(taskDTO, task));
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElse(null);

        if (null == task) {
            throw new RecordNotFoundException("Delete operation aborted. Couldn't find task with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}
