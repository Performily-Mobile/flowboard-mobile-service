package com.performily.flowboard.benefits.interfaces.rest;

import com.performily.flowboard.benefits.application.commandservices.BenefitCommandService;
import com.performily.flowboard.benefits.application.queryservices.BenefitQueryService;
import com.performily.flowboard.benefits.domain.model.commands.ActivateBenefitTypeCommand;
import com.performily.flowboard.benefits.domain.model.commands.DeactivateBenefitTypeCommand;
import com.performily.flowboard.benefits.domain.model.queries.GetAllBenefitTypesQuery;
import com.performily.flowboard.benefits.domain.model.queries.GetBenefitTypeByIdQuery;
import com.performily.flowboard.benefits.interfaces.rest.resources.BenefitTypeResource;
import com.performily.flowboard.benefits.interfaces.rest.resources.CreateBenefitTypeResource;
import com.performily.flowboard.benefits.interfaces.rest.transform.BenefitTypeResourceFromEntityAssembler;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.interfaces.rest.transform.ErrorResponseAssembler;
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
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
 * Benefit Types Controller
 * @summary
 * REST controller of the benefit catalog (US37). Types are never deleted, they are
 * deactivated, so the assignments that used them keep their history.
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/benefit-types", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Benefit Types", description = "Catalog of benefit types of the organization")
public class BenefitTypesController {
    private final BenefitCommandService benefitCommandService;
    private final BenefitQueryService benefitQueryService;

    public BenefitTypesController(BenefitCommandService benefitCommandService, BenefitQueryService benefitQueryService) {
        this.benefitCommandService = benefitCommandService;
        this.benefitQueryService = benefitQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a benefit type (US37)", description = "The name must be unique in the catalog.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Benefit type created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "A benefit type with that name already exists")
    })
    public ResponseEntity<?> createBenefitType(@Valid @RequestBody CreateBenefitTypeResource resource) {
        var result = benefitCommandService.handle(BenefitTypeResourceFromEntityAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, BenefitTypeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List the benefit catalog", description = "With activeOnly=true only the types that can be assigned.")
    public ResponseEntity<List<BenefitTypeResource>> getAllBenefitTypes(
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        return ResponseEntity.ok(benefitQueryService.handle(new GetAllBenefitTypesQuery(activeOnly)).stream()
                .map(BenefitTypeResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @GetMapping("/{benefitTypeId}")
    @Operation(summary = "Get a benefit type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Benefit type found"),
            @ApiResponse(responseCode = "404", description = "Benefit type not found")
    })
    public ResponseEntity<?> getBenefitTypeById(@PathVariable Long benefitTypeId) {
        return benefitQueryService.handle(new GetBenefitTypeByIdQuery(benefitTypeId))
                .<ResponseEntity<?>>map(type -> ResponseEntity.ok(BenefitTypeResourceFromEntityAssembler.toResourceFromEntity(type)))
                .orElseGet(() -> ErrorResponseAssembler.toErrorResponseFromApplicationError(
                        ApplicationError.notFound("BenefitType", String.valueOf(benefitTypeId))));
    }

    @PatchMapping("/{benefitTypeId}/activate")
    @Operation(summary = "Activate a benefit type")
    public ResponseEntity<?> activateBenefitType(@PathVariable Long benefitTypeId) {
        var result = benefitCommandService.handle(new ActivateBenefitTypeCommand(benefitTypeId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, BenefitTypeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @PatchMapping("/{benefitTypeId}/deactivate")
    @Operation(summary = "Deactivate a benefit type", description = "It stays in the catalog but cannot be assigned anymore.")
    public ResponseEntity<?> deactivateBenefitType(@PathVariable Long benefitTypeId) {
        var result = benefitCommandService.handle(new DeactivateBenefitTypeCommand(benefitTypeId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, BenefitTypeResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
