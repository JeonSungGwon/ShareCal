package com.example.Capstone.controller;

import com.example.Capstone.dto.ScheduleDto;
import com.example.Capstone.service.ScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/schedules")
public class ScheduleViewController {

    private final ScheduleService scheduleService;

    public ScheduleViewController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/schedule")
    public String getScheduleList(Model model) {
        List<ScheduleDto> schedules = scheduleService.getAllSchedules();
        model.addAttribute("schedules", schedules);
            return "schedule";
    }

    @GetMapping("/{id}")
    public String getSchedule(@PathVariable Long id, Model model) {
        ScheduleDto schedule = scheduleService.getScheduleById  (id);
        model.addAttribute("schedule", schedule);
        return "schedule/detail";
    }

    @GetMapping("/create")
    public String getCreateScheduleForm(Model model) {
        model.addAttribute("schedule", new ScheduleDto());
        return "schedule/create";
    }

    @PostMapping("/create")
    public String createSchedule(@ModelAttribute("schedule") @Validated ScheduleDto scheduleDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("오류입니다.");
            List<ObjectError> errors = bindingResult.getAllErrors();
            for (ObjectError error : errors) {
                System.out.println(error.getDefaultMessage());
            }
            return "schedule/create";
        }
        scheduleService.createSchedule(scheduleDto);
        return "redirect:/schedule";
    }

    @GetMapping("/{id}/update")
    public String getUpdateScheduleForm(@PathVariable Long id, Model model) {
        ScheduleDto schedule = scheduleService.getScheduleById(id);
        model.addAttribute("schedule", schedule);
        return "schedule/update";
    }

    @PostMapping("/{id}/update")
    public String updateSchedule(@PathVariable Long id, @ModelAttribute("schedule") @Validated ScheduleDto scheduleDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "schedule/update";
        }
        scheduleService.updateSchedule(id, scheduleDto);
        return "redirect:/schedules";
    }

    @PostMapping("/{id}/delete")
    public String deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return "redirect:/schedules";
    }
}