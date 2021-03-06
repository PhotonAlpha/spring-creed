package com.ethan.sso.controller;

import com.ethan.sso.dto.NoteDTO;
import com.ethan.sso.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    @Autowired
    NoteService noteService;

    @RequestMapping("/")
    public String home() {
        return "redirect:/notes";
    }

    @RequestMapping("/notes")
    public String notes(Model model) {
        model.addAttribute("notes", noteService.getAllNotes());
        return "index";
    }

    @RequestMapping("/add")
    public String add(Model model) {
        model.addAttribute("note", new NoteDTO());
        return "add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(NoteDTO noteDTO, Model model) {
        NoteDTO savedNote = noteService.addNote(noteDTO);

        if(savedNote != null){
            return "redirect:/notes";
        }else{
            model.addAttribute("note", noteDTO);
            return "add?error";
        }

    }
}
