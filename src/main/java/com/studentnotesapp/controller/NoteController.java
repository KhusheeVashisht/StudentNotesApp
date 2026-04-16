package com.studentnotesapp.controller;

import com.studentnotesapp.dto.ApiResponse;
import com.studentnotesapp.dto.NoteDto;
import com.studentnotesapp.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<NoteDto>> listNotes(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(noteService.getNotesByUsername(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDto> getNote(Authentication authentication, @PathVariable Long id) {
        String username = authentication.getName();
        return ResponseEntity.ok(noteService.getNoteById(username, id));
    }

    @PostMapping
    public ResponseEntity<NoteDto> createNote(Authentication authentication, @Valid @RequestBody NoteDto noteDto) {
        String username = authentication.getName();
        return ResponseEntity.ok(noteService.createNote(username, noteDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDto> updateNote(Authentication authentication,
                                              @PathVariable Long id,
                                              @Valid @RequestBody NoteDto noteDto) {
        String username = authentication.getName();
        return ResponseEntity.ok(noteService.updateNote(username, id, noteDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteNote(Authentication authentication, @PathVariable Long id) {
        String username = authentication.getName();
        noteService.deleteNote(username, id);
        return ResponseEntity.ok(new ApiResponse("Note deleted successfully."));
    }
}
