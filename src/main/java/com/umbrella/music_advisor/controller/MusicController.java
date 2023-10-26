package com.umbrella.music_advisor.controller;


import com.umbrella.music_advisor.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MusicController {

    private final SpotifyService spotifyService;

    @GetMapping("/")
    public String loginSuccessful(@RequestParam(name="code", required=false) String code) {
        spotifyService.setCode(code);
        System.out.println("Code received");
        System.out.println(code);
        if (code == null) {
            return "Authorization code not found. Try again.";
        }
        return "Got the code. Return back to your program.";
    }
}
