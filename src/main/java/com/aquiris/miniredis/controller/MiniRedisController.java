package com.aquiris.miniredis.controller;

import com.aquiris.miniredis.entity.NElement;
import com.aquiris.miniredis.model.DeleteKeysBody;
import com.aquiris.miniredis.model.ZElementsBody;
import com.aquiris.miniredis.service.MiniRedisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/mini-redis")
public class MiniRedisController {

    private final MiniRedisService miniRedisService;

    MiniRedisController(MiniRedisService miniRedisService) {
        this.miniRedisService = miniRedisService;
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public Integer deleteKeysFromBothDBs(@RequestBody DeleteKeysBody deleteKeysBody) {
        return miniRedisService.deleteKeys(deleteKeysBody.getKeys(), "both");
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Long getDBSizeFromBothDBs() {
        return miniRedisService.getDBSize("both");
    }

    @DeleteMapping("/element")
    @ResponseStatus(HttpStatus.OK)
    public Integer deleteKeysFromN(@RequestBody DeleteKeysBody deleteKeysBody) {
        return miniRedisService.deleteKeys(deleteKeysBody.getKeys(), "nElement");
    }

    @GetMapping("/element")
    @ResponseStatus(HttpStatus.OK)
    public Long getDBSizeFromN() {
        return miniRedisService.getDBSize("nElement");
    }

    @PostMapping("/element/{key}")
    @ResponseStatus(HttpStatus.OK)
    public NElement setKeyValue(@PathVariable final String key,
                                @RequestParam("value") final String value,
                                @RequestParam("seconds") final Optional<Integer> seconds) {
        if (seconds.isPresent()) return miniRedisService.setValue(key, value, seconds.get());
        return miniRedisService.setValue(key, value);
    }

    @GetMapping("/element/{key}")
    @ResponseStatus(HttpStatus.OK)
    public String getKeyValue(@PathVariable final String key) {
        return miniRedisService.getValue(key);
    }

    @PutMapping("/element/{key}")
    @ResponseStatus(HttpStatus.OK)
    public String increaseKeyValue(@PathVariable final String key) {
        return miniRedisService.increaseValue(key);
    }

    @DeleteMapping("/sorted-set")
    @ResponseStatus(HttpStatus.OK)
    public Integer deleteKeysFromZ(@RequestBody DeleteKeysBody deleteKeysBody) {
        return miniRedisService.deleteKeys(deleteKeysBody.getKeys(), "sortedSet");
    }

    @GetMapping("/sorted-set")
    @ResponseStatus(HttpStatus.OK)
    public Long getDBSizeFromZ() {
        return miniRedisService.getDBSize("sortedSet");
    }

    @PutMapping("/sorted-set/{key}")
    @ResponseStatus(HttpStatus.OK)
    public Integer zAddKeyScoreMember(@PathVariable final String key,
                                      @RequestBody ZElementsBody zElementsBody) {
        return miniRedisService.zAddScoreMember(key, zElementsBody.getZElements());
    }

    @GetMapping("/sorted-set/{key}/card")
    @ResponseStatus(HttpStatus.OK)
    public Integer zCardinality(@PathVariable final String key) {
        return miniRedisService.zCardinality(key);
    }

    @GetMapping("/sorted-set/{key}/rank")
    @ResponseStatus(HttpStatus.OK)
    public Integer zRankKeyMember(@PathVariable final String key,
                                  @RequestParam("member") final String member) {
        return miniRedisService.zRankMember(key, member);
    }

    @GetMapping("/sorted-set/{key}/range")
    @ResponseStatus(HttpStatus.OK)
    public ZElementsBody zRangeKeyStartStop(@PathVariable final String key,
                                            @RequestParam("start") final Integer start,
                                            @RequestParam("stop") final Integer stop) {
        return new ZElementsBody(miniRedisService.zRangeKey(key, start, stop));
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> executeCommandLine(@RequestParam("cmd") final String commandLine) {
        return new ResponseEntity<>(miniRedisService.orchestrateCommandLine(commandLine), HttpStatus.OK);
    }

}
