package com.ethan.app1.service;


import com.ethan.app1.dto.NoteDTO;

import java.util.Collection;

/**
 * Created by jjmendoza on 18/7/2017.
 */
public interface NoteService {
    Collection<NoteDTO> getAllNotes();
    NoteDTO addNote(NoteDTO note);
}
