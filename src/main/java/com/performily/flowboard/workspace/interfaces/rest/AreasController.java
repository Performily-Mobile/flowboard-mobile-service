package com.performily.flowboard.workspace.interfaces.rest;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.performily.flowboard.workspace.application.commandservices.AreaCommandService;
import com.performily.flowboard.workspace.application.queryservices.AreaQueryService;
import com.performily.flowboard.workspace.domain.model.commands.ActivateAreaCommand;
import com.performily.flowboard.workspace.domain.model.commands.DeactivateAreaCommand;
import com.performily.flowboard.workspace.domain.model.entities.Area;
import com.performily.flowboard.workspace.domain.model.queries.GetAllAreasQuery;
import com.performily.flowboard.workspace.domain.model.queries.GetAreaByIdQuery;
import com.performily.flowboard.workspace.interfaces.rest.resources.AreaResource;
import com.performily.flowboard.workspace.interfaces.rest.resources.CreateAreaResource;
import com.performily.flowboard.workspace.interfaces.rest.resources.RenameAreaResource;
import com.performily.flowboard.workspace.interfaces.rest.transform.AreaResourceFromEntityAssembler;
import com.performily.flowboard.workspace.interfaces.rest.transform.CreateAreaCommandFromResourceAssembler;
import com.performily.flowboard.workspace.interfaces.rest.transform.RenameAreaCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Areas Controller
 * @summary
 * REST controller that exposes the organizational areas.
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/areas", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Areas", description = "Organizational area management endpoints")
public class AreasController {
    private final AreaCommandService areaCommandService;
    private final AreaQueryService areaQueryService;

    /**
     * Constructor.
     *
     * @param areaCommandService the {@link AreaCommandService} instance
     * @param areaQueryService the {@link AreaQueryService} instance
     */
    public AreasController(AreaCommandService areaCommandService, AreaQueryService areaQueryService) {
        this.areaCommandService = areaCommandService;
        this.areaQueryService = areaQueryService;
    }

    /**
     * Create a new area.
     *
     * @param resource the {@link CreateAreaResource} instance
     * @return the HTTP response
     */
    @PostMapping
    @Operation(summary = "Create a new area", description = "Creates a new active area. The name must be unique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Area created successfully",
                    content = @Content(schema = @Schema(implementation = AreaResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Conflict - area name already exists")
    })
    public ResponseEntity<?> createArea(@Valid @RequestBody CreateAreaResource resource) {
        var createAreaCommand = CreateAreaCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = areaCommandService.handle(createAreaCommand)
                .flatMap(areaId -> areaQueryService.handle(new GetAreaByIdQuery(areaId))
                        .<Result<Area, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("Area", areaId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                AreaResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    /**
     * Get all areas.
     *
     * @return the HTTP response
     */
    @GetMapping
    @Operation(summary = "Get all areas", description = "Retrieves all the organizational areas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Areas retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AreaResource.class)))
    })
    public ResponseEntity<List<AreaResource>> getAllAreas() {
        var areas = areaQueryService.handle(new GetAllAreasQuery());
        var areaResources = areas.stream().map(AreaResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(areaResources);
    }

    /**
     * Get area by ID.
     *
     * @param areaId the area id
     * @return the HTTP response
     */
    @GetMapping("/{areaId}")
    @Operation(summary = "Get area by ID", description = "Retrieves a specific area by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Area found",
                    content = @Content(schema = @Schema(implementation = AreaResource.class))),
            @ApiResponse(responseCode = "404", description = "Area not found")
    })
    public ResponseEntity<?> getAreaById(
            @PathVariable @Parameter(description = "Area unique identifier", example = "1", required = true) Long areaId) {
        var area = areaQueryService.handle(new GetAreaByIdQuery(areaId));
        if (area.isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(ApplicationError.notFound("Area", areaId.toString()));
        }
        return ResponseEntity.ok(AreaResourceFromEntityAssembler.toResourceFromEntity(area.get()));
    }

    /**
     * Rename area.
     *
     * @param areaId the area id
     * @param resource the {@link RenameAreaResource} instance
     * @return the HTTP response
     */
    @PutMapping("/{areaId}")
    @Operation(summary = "Rename area", description = "Renames an existing area. The name must be unique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Area renamed successfully",
                    content = @Content(schema = @Schema(implementation = AreaResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Area not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - area name already exists")
    })
    public ResponseEntity<?> renameArea(
            @PathVariable @Parameter(description = "Area unique identifier", example = "1", required = true) Long areaId,
            @Valid @RequestBody RenameAreaResource resource) {
        var renameAreaCommand = RenameAreaCommandFromResourceAssembler.toCommandFromResource(areaId, resource);
        var result = areaCommandService.handle(renameAreaCommand);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, AreaResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Activate area.
     *
     * @param areaId the area id
     * @return the HTTP response
     */
    @PatchMapping("/{areaId}/activate")
    @Operation(summary = "Activate area", description = "Activates an inactive area.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Area activated successfully",
                    content = @Content(schema = @Schema(implementation = AreaResource.class))),
            @ApiResponse(responseCode = "404", description = "Area not found"),
            @ApiResponse(responseCode = "422", description = "Area is already active")
    })
    public ResponseEntity<?> activateArea(
            @PathVariable @Parameter(description = "Area unique identifier", example = "1", required = true) Long areaId) {
        var result = areaCommandService.handle(new ActivateAreaCommand(areaId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, AreaResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Deactivate area.
     *
     * @param areaId the area id
     * @return the HTTP response
     */
    @PatchMapping("/{areaId}/deactivate")
    @Operation(summary = "Deactivate area", description = "Deactivates an area. An area with ACTIVE employees cannot be deactivated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Area deactivated successfully",
                    content = @Content(schema = @Schema(implementation = AreaResource.class))),
            @ApiResponse(responseCode = "404", description = "Area not found"),
            @ApiResponse(responseCode = "422", description = "Area has active employees or is already inactive")
    })
    public ResponseEntity<?> deactivateArea(
            @PathVariable @Parameter(description = "Area unique identifier", example = "1", required = true) Long areaId) {
        var result = areaCommandService.handle(new DeactivateAreaCommand(areaId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, AreaResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}