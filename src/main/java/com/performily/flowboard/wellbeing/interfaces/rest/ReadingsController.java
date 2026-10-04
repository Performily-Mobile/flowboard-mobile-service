package com.performily.flowboard.wellbeing.interfaces.rest;

import com.performily.flowboard.wellbeing.application.commandservices.WellbeingCommandService;
import com.performily.flowboard.wellbeing.domain.model.commands.RegisterReadingCommand;
import com.performily.flowboard.wellbeing.interfaces.rest.resources.RegisterReadingResource;
import com.performily.flowboard.wellbeing.interfaces.rest.transform.ReadingResourceFromEntityAssembler;
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/readings")
@Tag(name = "Wellbeing - Readings", description = "Environmental readings sent by linked devices (test data)")
public class ReadingsController {

    private final WellbeingCommandService commandService;

    public ReadingsController(WellbeingCommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping
    @Operation(summary = "Register a reading of a linked device, it is classified with the office threshold")
    public ResponseEntity<?> registerReading(@Valid @RequestBody RegisterReadingResource r) {
        var result = commandService.handle(new RegisterReadingCommand(r.deviceCode(), r.metricType(), r.value(), r.recordedAt()));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, ReadingResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }
}
