package Capstone.service;

import com.example.Capstone.config.SecurityUtil;
import com.example.Capstone.dto.TodoDTO;
import com.example.Capstone.entity.Member;
import com.example.Capstone.entity.Todo;
import com.example.Capstone.repository.MemberRepository;
import com.example.Capstone.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {
    private TodoRepository todoRepository;
    private MemberRepository memberRepository;

    public TodoService(TodoRepository todoRepository, MemberRepository memberRepository) {
        this.todoRepository = todoRepository;
        this.memberRepository = memberRepository;
    }

    public List<TodoDTO> getAllTodos() {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId()).orElse(null);
        List<Todo> todos = todoRepository.findByMember(member);
        return todos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TodoDTO createTodo(TodoDTO todoDTO) {
        Todo todo = convertToEntity(todoDTO);
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId()).orElse(null);
        todo.setMember(member);
        Todo savedTodo = todoRepository.save(todo);
        return convertToDTO(savedTodo);
    }

    public TodoDTO updateTodo(Long id, TodoDTO todoDTO) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo id: " + id));
        if(todoDTO.getTitle()!=null&&todoDTO.getTitle().isEmpty()) {
            todo.setTitle(todoDTO.getTitle());
        }

        if (todoDTO.isCompleted() != todo.isCompleted()) {
            todo.setCompleted(todoDTO.isCompleted());
        }
        Todo updatedTodo = todoRepository.save(todo);
        return convertToDTO(updatedTodo);
    }

    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }

    private TodoDTO convertToDTO(Todo todo) {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setId(todo.getId());
        todoDTO.setTitle(todo.getTitle());
        todoDTO.setCompleted(todo.isCompleted());
        todoDTO.setDueDate(todo.getDueDate());
        todoDTO.setMemberId(todo.getId());
        todoDTO.setMemberNickname(todo.getMember().getNickname());
        return todoDTO;
    }

    private Todo convertToEntity(TodoDTO todoDTO) {
        Todo todo = new Todo();
        todo.setTitle(todoDTO.getTitle());
        todo.setCompleted(todoDTO.isCompleted());
        todo.setDueDate(todoDTO.getDueDate());
        return todo;
    }
}

