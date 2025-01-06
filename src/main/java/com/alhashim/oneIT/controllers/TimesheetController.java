package com.alhashim.oneIT.controllers;

import com.alhashim.oneIT.models.Employee;
import com.alhashim.oneIT.models.EmployeeCalendar;
import com.alhashim.oneIT.models.ShiftSchedule;
import com.alhashim.oneIT.repositories.EmployeeCalendarRepository;
import com.alhashim.oneIT.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/timesheet")
public class TimesheetController {

    @Autowired
    EmployeeCalendarRepository employeeCalendarRepository;

    @Autowired
    EmployeeRepository employeeRepository;


    // only for HR & admin
    @GetMapping("/list")
    public String timesheetList(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Page<EmployeeCalendar> employeeCalendarPage;

        //for search
        if(keyword !=null && !keyword.isEmpty())
        {
            // Implement a paginated search query in your repository
            employeeCalendarPage = employeeCalendarRepository.findByKeyword(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        else
        {
            // Fetch all employees with pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            employeeCalendarPage = employeeCalendarRepository.findAll(pageable);
        }

        model.addAttribute("timesheets", employeeCalendarPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeeCalendarPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", employeeCalendarPage.getTotalElements());
        model.addAttribute("pageTitle","Timesheet List");

        // ---
        return "/timesheet/list";
    }

    // only for HR and admin
    @GetMapping("/detail")
    public String timesheetDetail(Model model, @RequestParam Long id)
    {
        EmployeeCalendar employeeCalendar = employeeCalendarRepository.findById(id).orElse(null);
        model.addAttribute("timesheet", employeeCalendar);
        return "/timesheet/detail";
    }


    //Only for him self
    @GetMapping("/employee")
    public String employeeTimesheet(@RequestParam Long id, Model model, @RequestParam(required = false)LocalDate startDate, @RequestParam(required = false) LocalDate endDate, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size)
    {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if(employee == null)
        {
            return "/404";
        }

        Page<EmployeeCalendar> employeeCalendarPage;

        if(startDate != null)
        {
            System.out.println("Filter Timesheet From: "+startDate +" To: "+endDate);
            employeeCalendarPage = employeeCalendarRepository.findByEmployeeFromTo(employee, startDate, endDate, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")) );
        }
        else
        {
            employeeCalendarPage = employeeCalendarRepository.findByEmployee(employee, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }

        model.addAttribute("employeeId", employee.getId());
        model.addAttribute("timesheets", employeeCalendarPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeeCalendarPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", employeeCalendarPage.getTotalElements());
        model.addAttribute("pageTitle","Timesheet Employee List");

        return "/timesheet/employeeTimesheet";
    }


    //---------Report
    @GetMapping("/report")
    public String report(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate, @RequestParam Long id, Model model) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee == null) {
            return "/404";
        }

        // Fetch Employee Calendars within the date range
        List<EmployeeCalendar> employeeCalendar = employeeCalendarRepository.findByEmployeeFromTo(employee, startDate, endDate);

        ShiftSchedule shiftSchedule = employee.getShiftSchedule();

        if(shiftSchedule ==null)
        {
            model.addAttribute("message", "shiftSchedule is null");
            return "/404";
        }

        // Group EmployeeCalendar by dayDate to handle multiple records for the same date
        Map<LocalDate, List<EmployeeCalendar>> calendarMap = employeeCalendar.stream()
                .collect(Collectors.groupingBy(EmployeeCalendar::getDayDate));

        // Create a list of all dates in the date range (including non-working days)
        List<LocalDate> allDates = getAllDatesInRange(startDate, endDate);

        // Prepare the timesheet data
        List<Map<String, Object>> timesheetData = new ArrayList<>();

        for (LocalDate date : allDates) {
            // Fetch EmployeeCalendar records for the current date
            List<EmployeeCalendar> calendarRecords = calendarMap.getOrDefault(date, Collections.emptyList());

            if (calendarRecords.isEmpty()) {
                // No record for this date, create a default entry (missing work day)
                timesheetData.add(createDefaultRecord(date, shiftSchedule));
            } else {
                // If multiple records exist for this date, loop through and add each one
                for (EmployeeCalendar calendarRecord : calendarRecords) {
                    timesheetData.add(createRecord(calendarRecord, shiftSchedule));
                }
            }
        }

        // Add data to the model
        model.addAttribute("timesheet", timesheetData);
        model.addAttribute("shiftSchedule", shiftSchedule);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "/timesheet/report";
    }

    private List<LocalDate> getAllDatesInRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        return dates;
    }

    private Map<String, Object> createDefaultRecord(LocalDate date, ShiftSchedule shiftSchedule) {
        Map<String, Object> record = new HashMap<>();
        record.put("date", date);
        record.put("checkIn", "No Record");
        record.put("checkOut", "No Record");
        record.put("shiftTimeStart", shiftSchedule != null ? shiftSchedule.getStartTime()  : "No Shift");
        record.put("shiftTimeEnd", shiftSchedule != null ? shiftSchedule.getEndTime()  : "No Shift");
        record.put("lateMinutes", 0);
        record.put("earlyMinutes", 0);
        record.put("totalMinutes", 0);
        record.put("totalMissingMinutes", 0);
        return record;
    }

    private Map<String, Object> createRecord(EmployeeCalendar calendarRecord, ShiftSchedule shiftSchedule) {
        Map<String, Object> record = new HashMap<>();
        record.put("date", calendarRecord.getDayDate());
        record.put("isWorkingDay", shiftSchedule.isWorkDay(calendarRecord.getDayDate().getDayOfWeek()));
        record.put("checkIn", calendarRecord.getCheckIn());
        record.put("checkOut", calendarRecord.getCheckOut());
        record.put("badgeNumber", calendarRecord.getEmployee().getBadgeNumber());
        record.put("name", calendarRecord.getEmployee().getName());
        record.put("areaIn", calendarRecord.getGeolocation() !=null ? calendarRecord.getGeolocation().getAreaName():" ");
        record.put("areaOut", calendarRecord.getGeolocationOUT() !=null ? calendarRecord.getGeolocationOUT().getAreaName() : " ");

        // Calculate late and early minutes
        int lateMinutes = calculateLateMinutes(calendarRecord, shiftSchedule);
        int earlyMinutes = calculateEarlyMinutes(calendarRecord, shiftSchedule);

        int totalMinutes = calendarRecord.getTotalMinutes();
        int totalMissingMinutes = lateMinutes + earlyMinutes;

        record.put("shiftTimeStart", shiftSchedule != null ? shiftSchedule.getStartTime()  : "No Shift");
        record.put("shiftTimeEnd", shiftSchedule != null ? shiftSchedule.getEndTime()  : "No Shift");
        record.put("lateMinutes", lateMinutes);
        record.put("earlyMinutes", earlyMinutes);
        record.put("totalMinutes", totalMinutes);
        record.put("totalMissingMinutes", totalMissingMinutes);

        return record;
    }

    private int calculateLateMinutes(EmployeeCalendar calendarRecord, ShiftSchedule shiftSchedule) {
        if (calendarRecord.getCheckIn() != null && shiftSchedule.getStartTime() != null) {
            LocalTime checkInTime = LocalTime.parse(calendarRecord.getCheckIn().toString());
            LocalTime shiftStartBuffer = shiftSchedule.getStartTime().plusMinutes(15);  // 15 min grace period

            if (checkInTime.isAfter(shiftStartBuffer)) {
                return (int) java.time.Duration.between(shiftStartBuffer, checkInTime).toMinutes();
            }
        }
        return 0;
    }

    private int calculateEarlyMinutes(EmployeeCalendar calendarRecord, ShiftSchedule shiftSchedule) {
        if (calendarRecord.getCheckOut() != null && shiftSchedule.getEndTime() != null) {
            LocalTime checkOutTime = LocalTime.parse(calendarRecord.getCheckOut().toString());
            LocalTime shiftEndBuffer = shiftSchedule.getEndTime().minusMinutes(10);  // 10 min tolerance

            if (checkOutTime.isBefore(shiftEndBuffer)) {
                return (int) java.time.Duration.between(checkOutTime, shiftEndBuffer).toMinutes();
            }
        }
        return 0;
    }

}
