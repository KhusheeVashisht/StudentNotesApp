package com.studentnotesapp.service;

import com.studentnotesapp.dto.NoteDto;
import com.studentnotesapp.entity.Note;
import com.studentnotesapp.entity.User;
import com.studentnotesapp.exception.ResourceNotFoundException;
import com.studentnotesapp.repository.NoteRepository;
import com.studentnotesapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public List<NoteDto> getNotesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        return noteRepository.findByUser(user).stream().map(this::toDto).collect(Collectors.toList());
    }

    public NoteDto getNoteById(String username, Long id) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found."));
        return toDto(note);
    }

    public NoteDto createNote(String username, NoteDto noteDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        Note note = new Note();
        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        note.setUser(user);
        Note saved = noteRepository.save(note);
        return toDto(saved);
    }

    public NoteDto updateNote(String username, Long id, NoteDto noteDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found."));
        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        Note updated = noteRepository.save(note);
        return toDto(updated);
    }

    public void deleteNote(String username, Long id) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found."));
        noteRepository.delete(note);
    }

    private NoteDto toDto(Note note) {
        NoteDto dto = new NoteDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        return dto;
    }
}
