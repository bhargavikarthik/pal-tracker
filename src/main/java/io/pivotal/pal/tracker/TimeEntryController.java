package io.pivotal.pal.tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {

    private final CounterService counter;
    private final GaugeService gauge;

    TimeEntryRepository timeEntryRepository;

    public TimeEntryController(TimeEntryRepository timeEntryRepository,CounterService counter, GaugeService gauge) {
        this.timeEntryRepository = timeEntryRepository;
        this.counter = counter;
        this.gauge = gauge;

    }


    @PostMapping("/time-entries")
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntryToCreate) {
        TimeEntry te = timeEntryRepository.create(timeEntryToCreate);
        counter.increment("TimeEntry.created");
        gauge.submit("timeEntries.count", timeEntryRepository.list().size());
       return new ResponseEntity(te, HttpStatus.CREATED);
    }

    @GetMapping("/time-entries/{timeEntryId}")
    public ResponseEntity<TimeEntry> read(@PathVariable long timeEntryId) {
        TimeEntry timeEntry=timeEntryRepository.find(timeEntryId);
        if (timeEntry!=null) {
            counter.increment("TimeEntry.read");
        return new ResponseEntity(timeEntry,HttpStatus.OK); }
        else
            return new ResponseEntity(timeEntry,HttpStatus.NOT_FOUND);
    }


    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list() {
        counter.increment("TimeEntry.listed");
        return new ResponseEntity(timeEntryRepository.list(),HttpStatus.OK);
    }

    @PutMapping("/time-entries/{timeEntryId}")
    public ResponseEntity update(@PathVariable long timeEntryId, @RequestBody TimeEntry entry) {

        TimeEntry timeEntry=timeEntryRepository.update(timeEntryId, entry);
        if (timeEntry!=null) {
            counter.increment("TimeEntry.updated");
            return new ResponseEntity(timeEntry,HttpStatus.OK); }
        else
            return new ResponseEntity(timeEntry,HttpStatus.NOT_FOUND);

    }

    @DeleteMapping("/time-entries/{timeEntryId}")
    public ResponseEntity<TimeEntry> delete(@PathVariable long timeEntryId) {
        timeEntryRepository.delete(timeEntryId);
        counter.increment("TimeEntry.deleted");
        gauge.submit("timeEntries.count", timeEntryRepository.list().size());
        return new ResponseEntity( HttpStatus.NO_CONTENT);

    }
}



