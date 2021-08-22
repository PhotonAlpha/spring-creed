package com.ethan.sso.service.impl;

import com.ethan.sso.dto.NoteDTO;
import com.ethan.sso.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
// import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class NoteServiceImpl implements NoteService {
    @Autowired
    OAuth2RestTemplate restTemplate;

    @Value("${resource-server}/note")
    private String notesURL;

    @Override
    public Collection<NoteDTO> getAllNotes() {
        // Resources<NoteDTO> notes = restTemplate.getForObject(notesURL, Resources.class);
        // return notes.getContent();
        return null;
    }

    @Override
    public NoteDTO addNote(NoteDTO note) {
        ResponseEntity<NoteDTO> response = restTemplate.postForEntity(notesURL, note, NoteDTO.class);
        return response.getBody();
    }
}
