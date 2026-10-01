package com.performily.flowboard.workspace.interfaces.rest;

import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import com.performily.flowboard.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.performily.flowboard.workspace.application.commandservices.PositionCommandService;
import com.performily.flowboard.workspace.application.queryservices.PositionQueryService;
import com.performily.flowboard.workspace.domain.model.commands.DeactivatePositionCommand;
import com.performily.flowboard.workspace.domain.model.entities.Position;
import com.performily.flowboard.workspace.domain.model.queries.GetAllPositionsByAreaIdQuery;
import com.performily.flowboard.workspace.domain.model.queries.GetAllPositionsQuery;
import com.performily.flowboard.workspace.domain.model.queries.GetPositionByIdQuery;
import com.performily.flowboard.workspace.interfaces.rest.resources.CreatePositionResource;
import com.performily.flowboard.workspace.interfaces.rest.resources.PositionResource;
import com.performily.flowboard.workspace.interfaces.rest.resources.UpdatePositionReferenceSalaryResource;
import com.performily.flowboard.workspace.interfaces.rest.transform.CreatePositionCommandFromResourceAssembler;
import com.performily.flowboard.workspace.interfaces.rest.transform.PositionResourceFromEntityAssembler;
import com.performily.flowboard.workspace.interfaces.rest.transform.UpdatePositionReferenceSalaryCommandFromResourceAssembler;
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
 * Positions Controller
 * @summary
 * REST controller that exposes the job positions and their reference salary.
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/positions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Positions", description = "Job position management endpoints")
public class PositionsController {
    private final PositionCommandService positionCommandService;
    private final PositionQueryService positionQueryService;

    /**
     * Constructor.
     *
     * @param positionCommandService the {@link PositionCommandService} instance
     * @param positionQueryService the {@link PositionQueryService} instance
     */
    public PositionsController(PositionCommandService positionCommandService, PositionQueryService positionQueryService) {
        this.positionCommandService = positionCommandService;
        this.positionQueryService = positionQueryService;
    }

    /**
     * Create a new position.
     *
     * @param resource the {@link CreatePositionResource} instance
     * @return the HTTP response
     */
    @PostMapping
    @Operation(summary = "Create a new position", description = "Creates a new active position inside an active area.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Position created successfully",
                    content = @Content(schema = @Schema(implementation = PositionResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Area not found"),
            @ApiResponse(responseCode = "422", description = "Area is inactive")
    })
    public ResponseEntity<?> createPosition(@Valid @RequestBody CreatePositionResource resource) {
        var createPositionCommand = CreatePositionCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = positionCommandService.handle(createPositionCommand)
                .flatMap(positionId -> positionQueryService.handle(new GetPositionByIdQuery(positionId))
                        .<Result<Position, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("Position", positionId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, PositionResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    /**
     * Get all positions.
     *
     * @param areaId the area id
     * @return the HTTP response
     */
    @GetMapping
    @Operation(summary = "Get all positions", description = "Retrieves all positions, or only the positions of an area when areaId is sent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Positions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PositionResource.class)))
    })
    public ResponseEntity<List<PositionResource>> getAllPositions(
            @RequestParam(required = false) @Parameter(description = "Filter by area identifier", example = "1") Long areaId) {
        var positions = areaId == null
                ? positionQueryService.handle(new GetAllPositionsQuery())
                : positionQueryService.handle(new GetAllPositionsByAreaIdQuery(areaId));
        var positionResources = positions.stream().map(PositionResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(positionResources);
    }

    /**
     * Get position by ID.
     *
     * @param positionId the position id
     * @return the HTTP response
     */
    @GetMapping("/{positionId}")
    @Operation(summary = "Get position by ID", description = "Retrieves a specific position by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Position found",
                    content = @Content(schema = @Schema(implementation = PositionResource.class))),
            @ApiResponse(responseCode = "404", description = "Position not found")
    })
    public ResponseEntity<?> getPositionById(
            @PathVariable @Parameter(description = "Position unique identifier", example = "1", required = true) Long positionId) {
        var position = positionQueryService.handle(new GetPositionByIdQuery(positionId));
        if (position.isEmpty()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(ApplicationError.notFound("Position", positionId.toString()));
        }
        return ResponseEntity.ok(PositionResourceFromEntityAssembler.toResourceFromEntity(position.get()));
    }

    /**
     * Update reference salary.
     *
     * @param positionId the position id
     * @param resource the {@link UpdatePositionReferenceSalaryResource} instance
     * @return the HTTP response
     */
    @PatchMapping("/{positionId}/reference-salary")
    @Operation(summary = "Update reference salary", description = "Updates the reference salary of a position.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reference salary updated successfully",
                    content = @Content(schema = @Schema(implementation = PositionResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Position not found")
    })
    public ResponseEntity<?> updateReferenceSalary(
            @PathVariable @Parameter(description = "Position unique identifier", example = "1", required = true) Long positionId,
            @Valid @RequestBody UpdatePositionReferenceSalaryResource resource) {
        var command = UpdatePositionReferenceSalaryCommandFromResourceAssembler.toCommandFromResource(positionId, resource);
        var result = positionCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, PositionResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    /**
     * Deactivate position.
     *
     * @param positionId the position id
     * @return the HTTP response
     */
    @PatchMapping("/{positionId}/deactivate")
    @Operation(summary = "Deactivate position", description = "Deactivates a position.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Position deactivated successfully",
                    content = @Content(schema = @Schema(implementation = PositionResource.class))),
            @ApiResponse(responseCode = "404", description = "Position not found"),
            @ApiResponse(responseCode = "422", description = "Position is already inactive")
    })
    public ResponseEntity<?> deactivatePosition(
            @PathVariable @Parameter(description = "Position unique identifier", example = "1", required = true) Long positionId) {
        var result = positionCommandService.handle(new DeactivatePositionCommand(positionId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, PositionResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}