package com.performily.flowboard.benefits.interfaces.rest;

import com.performily.flowboard.benefits.application.commandservices.BenefitCommandService;
import com.performily.flowboard.benefits.application.queryservices.BenefitQueryService;
import com.performily.flowboard.benefits.domain.model.commands.CancelBenefitAssignmentCommand;
import com.performily.flowboard.benefits.domain.model.commands.RegisterBenefitDeliveryCommand;
import com.performily.flowboard.benefits.domain.model.queries.GetAreaAssignmentPreviewQuery;
import com.performily.flowboard.benefits.domain.model.queries.GetBenefitAssignmentsByEmployeeIdQuery;
import com.performily.flowboard.benefits.domain.model.queries.GetBenefitAssignmentsQuery;
import com.performily.flowboard.benefits.domain.model.valueobjects.AssignmentStatus;
import com.performily.flowboard.benefits.interfaces.rest.resources.AssignBenefitResource;
import com.performily.flowboard.benefits.interfaces.rest.resources.BenefitAssignmentResource;
import com.performily.flowboard.benefits.interfaces.rest.resources.RegisterDeliveryResource;
import com.performily.flowboard.benefits.interfaces.rest.transform.AssignBenefitCommandFromResourceAssembler;
import com.performily.flowboard.benefits.interfaces.rest.transform.BenefitAssignmentResourceFromEntityAssembler;
import com.performily.flowboard.benefits.interfaces.rest.transform.BenefitsEnumAssembler;
import com.performily.flowboard.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Benefits Controller
 * @summary
 * REST controller of the benefit assignments (US38), their delivery (US39) and the
 * benefits of an employee (US40).
 *
 * Until IAM is implemented, /me receives the employeeId as a parameter and the HR
 * employee that registers a delivery is sent by the client. When IAM exists they
 * will be taken from the token.
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/v1/benefits", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Benefits", description = "Benefit assignments, deliveries and benefits of an employee")
public class BenefitsController {
    private final BenefitCommandService benefitCommandService;
    private final BenefitQueryService benefitQueryService;

    public BenefitsController(BenefitCommandService benefitCommandService, BenefitQueryService benefitQueryService) {
        this.benefitCommandService = benefitCommandService;
        this.benefitQueryService = benefitQueryService;
    }

    @PostMapping
    @Operation(summary = "Assign a benefit to an employee or to an area (US38)",
            description = "With employeeId it creates one assignment. With areaId it creates one per active employee "
                    + "of the area and skips who already has the benefit in the period.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Benefit assigned"),
            @ApiResponse(responseCode = "400", description = "Invalid data or both employeeId and areaId sent"),
            @ApiResponse(responseCode = "404", description = "Benefit type or employee not found"),
            @ApiResponse(responseCode = "409", description = "The employee already has the benefit in an overlapping period"),
            @ApiResponse(responseCode = "422", description = "Inactive employee or benefit type, or area without active employees")
    })
    public ResponseEntity<?> assignBenefit(@Valid @RequestBody AssignBenefitResource resource) {
        if (AssignBenefitCommandFromResourceAssembler.isForArea(resource)) {
            var result = benefitCommandService.handle(AssignBenefitCommandFromResourceAssembler.toAreaCommandFromResource(resource));
            return ResponseEntityAssembler.toResponseEntityFromResult(
                    result, BenefitAssignmentResourceFromEntityAssembler::toBatchResource, HttpStatus.CREATED);
        }
        var result = benefitCommandService.handle(AssignBenefitCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, BenefitAssignmentResourceFromEntityAssembler::toBatchResource, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List assignments for HR staff",
            description = "status=ASSIGNED for \"Por entregar\" and status=DELIVERED for \"Entregados\".")
    public ResponseEntity<List<BenefitAssignmentResource>> getAssignments(
            @Parameter(description = "ASSIGNED, DELIVERED or CANCELLED") @RequestParam(required = false) String status,
            @RequestParam(required = false) Long benefitTypeId) {
        var assignmentStatus = BenefitsEnumAssembler.toEnum(AssignmentStatus.class, status, "status");
        return ResponseEntity.ok(benefitQueryService.handle(new GetBenefitAssignmentsQuery(assignmentStatus, benefitTypeId)).stream()
                .map(BenefitAssignmentResourceFromEntityAssembler::toResourceFromView).toList());
    }

    @GetMapping("/area-preview")
    @Operation(summary = "Preview an assignment to an area (US38)",
            description = "How many active employees will receive the benefit and how many will be skipped.")
    public ResponseEntity<?> previewAreaAssignment(
            @RequestParam Long benefitTypeId,
            @RequestParam Long areaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        var result = benefitQueryService.handle(new GetAreaAssignmentPreviewQuery(benefitTypeId, areaId, startDate, endDate));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, BenefitAssignmentResourceFromEntityAssembler::toResourceFromView, HttpStatus.OK);
    }

    @GetMapping("/me")
    @Operation(summary = "Benefits of the employee: current and delivered (US40)",
            description = "Returns empty lists when the employee has no benefits. employeeId will come from the token when IAM exists.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Benefits of the employee"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<?> getMyBenefits(@RequestParam Long employeeId) {
        var result = benefitQueryService.handle(new GetBenefitAssignmentsByEmployeeIdQuery(employeeId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, BenefitAssignmentResourceFromEntityAssembler::toResourceFromView, HttpStatus.OK);
    }

    @PostMapping("/{assignmentId}/deliveries")
    @Operation(summary = "Register the delivery of a benefit (US39)", description = "A delivery can be registered only once.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Delivery registered"),
            @ApiResponse(responseCode = "400", description = "Invalid date"),
            @ApiResponse(responseCode = "404", description = "Assignment not found"),
            @ApiResponse(responseCode = "422", description = "Already delivered or cancelled")
    })
    public ResponseEntity<?> registerDelivery(@PathVariable Long assignmentId,
                                              @Valid @RequestBody RegisterDeliveryResource resource) {
        var result = benefitCommandService.handle(new RegisterBenefitDeliveryCommand(
                assignmentId, resource.deliveredOn(), resource.registeredById(), resource.notes()));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, BenefitAssignmentResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PatchMapping("/{assignmentId}/cancel")
    @Operation(summary = "Cancel an assignment that was not delivered")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assignment cancelled"),
            @ApiResponse(responseCode = "404", description = "Assignment not found"),
            @ApiResponse(responseCode = "422", description = "The benefit was already delivered or cancelled")
    })
    public ResponseEntity<?> cancelAssignment(@PathVariable Long assignmentId) {
        var result = benefitCommandService.handle(new CancelBenefitAssignmentCommand(assignmentId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, BenefitAssignmentResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
