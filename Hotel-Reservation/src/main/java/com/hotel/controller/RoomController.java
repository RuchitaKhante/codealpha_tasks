package com.hotel.controller;

import com.hotel.entity.Room;
import com.hotel.service.RoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // Show available rooms (with optional type filter)
    @GetMapping
    public String showRooms(@RequestParam(required = false) String type, Model model) {
        List<Room> rooms;

        if (type != null && !type.isEmpty()) {
            try {
                Room.RoomType roomType = Room.RoomType.valueOf(type.toUpperCase());
                rooms = roomService.getAvailableRoomsByType(roomType);
                model.addAttribute("selectedType", type.toUpperCase());
            } catch (IllegalArgumentException e) {
                rooms = roomService.getAvailableRooms();
            }
        } else {
            rooms = roomService.getAvailableRooms();
        }

        model.addAttribute("rooms", rooms);
        model.addAttribute("roomTypes", Room.RoomType.values());
        return "rooms";
    }
}