package com.performily.flowboard.wellbeing.interfaces.rest;

import com.performily.flowboard.wellbeing.application.commandservices.WellbeingCommandService;
import com.performily.flowboard.wellbeing.application.queryservices.WellbeingQueryService;
import com.performily.flowboard.wellbeing.domain.model.commands.*;
import com.performily.flowboard.wellbeing.domain.model.queries.GetOfficeStatusQuery;
import com.performily.flowboard.wellbeing.domain.model.queries.GetReadingHistoryQuery;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.MetricType;
import com.performily.flowboard.wellbeing.interfaces.rest.resources.*;
import com.performily.flowboard.wellbeing.interfaces.rest.transform.*;
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/offices")
@Tag(name = "Wellbeing - Offices", description = "Workspaces, linked devices, thresholds and environmental indicators")
public class OfficesController {

    private final WellbeingCommandService commandService;
    private final WellbeingQueryService queryService;

    public OfficesController(WellbeingCommandService commandService, WellbeingQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Register a workspace (US47)")
    public ResponseEntity<?> createOffice(@Valid @RequestBody CreateOfficeResource r) {
        var result = commandService.handle(new CreateOfficeCommand(r.name(), r.area(), r.address(), r.floor(), r.reference()));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, OfficeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List workspaces with their current environmental indicator (US47, US50)")
    public ResponseEntity<List<OfficeStatusResource>> getAllOffices() {
        return ResponseEntity.ok(queryService.getAllOfficeStatuses().stream()
                .map(OfficeStatusResourceFromViewAssembler::toResourceFromView)
                .toList());
    }

    @GetMapping("/{officeId}/status")
    @Operation(summary = "Get the environmental indicators of a workspace (US50)")
    public ResponseEntity<?> getOfficeStatus(@PathVariable Long officeId) {
        return queryService.handle(new GetOfficeStatusQuery(officeId))
                .<ResponseEntity<?>>map(v -> ResponseEntity.ok(OfficeStatusResourceFromViewAssembler.toResourceFromView(v)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{officeId}/devices")
    @Operation(summary = "Link a device of the inventory to a workspace (US48)")
    public ResponseEntity<?> linkDevice(@PathVariable Long officeId, @Valid @RequestBody LinkDeviceResource r) {
        var result = commandService.handle(new LinkDeviceCommand(officeId, r.deviceCode()));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, DeviceResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{officeId}/devices/{deviceCode}")
    @Operation(summary = "Unlink a device from a workspace, it returns to the inventory (US48)")
    public ResponseEntity<?> unlinkDevice(@PathVariable Long officeId, @PathVariable String deviceCode) {
        var result = commandService.handle(new UnlinkDeviceCommand(officeId, deviceCode));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, DeviceResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @GetMapping("/{officeId}/thresholds")
    @Operation(summary = "Get the thresholds configured for a workspace (US49)")
    public ResponseEntity<List<MetricThresholdResource>> getThresholds(@PathVariable Long officeId) {
        return ResponseEntity.ok(queryService.getThresholdsByOfficeId(officeId).stream()
                .map(MetricThresholdResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }

    @PutMapping("/{officeId}/thresholds/{metricType}")
    @Operation(summary = "Define or redefine the ranges of a metric (US49)")
    public ResponseEntity<?> defineThreshold(@PathVariable Long officeId, @PathVariable MetricType metricType,
                                             @Valid @RequestBody DefineMetricThresholdResource r) {
        var ranges = MetricThresholdResourceFromEntityAssembler.toRangesFromResource(r);
        var result = commandService.handle(new DefineMetricThresholdCommand(officeId, metricType, ranges));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, MetricThresholdResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @GetMapping("/{officeId}/readings")
    @Operation(summary = "Get the history of a metric in a date range (US51)")
    public ResponseEntity<?> getReadingHistory(@PathVariable Long officeId,
                                               @RequestParam MetricType metricType,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        var result = queryService.handle(new GetReadingHistoryQuery(officeId, metricType, from, to));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, ReadingHistoryResourceFromViewAssembler::toResourceFromView, HttpStatus.OK);
    }
}
