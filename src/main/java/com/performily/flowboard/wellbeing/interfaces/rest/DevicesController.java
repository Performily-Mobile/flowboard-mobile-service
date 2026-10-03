package com.performily.flowboard.wellbeing.interfaces.rest;

import com.performily.flowboard.wellbeing.application.commandservices.WellbeingCommandService;
import com.performily.flowboard.wellbeing.application.queryservices.WellbeingQueryService;
import com.performily.flowboard.wellbeing.domain.model.commands.RegisterDeviceCommand;
import com.performily.flowboard.wellbeing.domain.model.valueobjects.DeviceStatus;
import com.performily.flowboard.wellbeing.interfaces.rest.resources.DeviceResource;
import com.performily.flowboard.wellbeing.interfaces.rest.resources.RegisterDeviceResource;
import com.performily.flowboard.wellbeing.interfaces.rest.transform.DeviceResourceFromEntityAssembler;
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@Tag(name = "Wellbeing - Devices", description = "Inventory of environmental measuring devices")
public class DevicesController {

    private final WellbeingCommandService commandService;
    private final WellbeingQueryService queryService;

    public DevicesController(WellbeingCommandService commandService, WellbeingQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List inventory devices, optionally filtered by status (e.g. IN_INVENTORY)")
    public ResponseEntity<List<DeviceResource>> getDevices(@RequestParam(required = false) DeviceStatus status) {
        return ResponseEntity.ok(queryService.getDevices(status).stream()
                .map(DeviceResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }

    @PostMapping
    @Operation(summary = "Register a device in the inventory")
    public ResponseEntity<?> registerDevice(@Valid @RequestBody RegisterDeviceResource r) {
        var result = commandService.handle(new RegisterDeviceCommand(r.code(), r.supportedMetrics()));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, DeviceResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }
}
