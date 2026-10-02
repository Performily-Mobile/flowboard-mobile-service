package com.performily.flowboard.request.application.internal.commandservices;

import com.performily.flowboard.request.application.commandservices.RequestTypeCommandService;
import com.performily.flowboard.request.domain.model.commands.*;
import com.performily.flowboard.request.domain.model.entities.RequestField;
import com.performily.flowboard.request.domain.model.entities.RequestType;
import com.performily.flowboard.request.domain.model.valueobjects.FieldKey;
import com.performily.flowboard.request.domain.repositories.RequestRepository;
import com.performily.flowboard.request.domain.repositories.RequestTypeRepository;
import com.performily.flowboard.shared.application.result.ApplicationError;
import com.performily.flowboard.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * Request Type Command Service Impl
 * @summary
 * Application service that executes the commands over the request type catalog.
 * The name of a type is unique, and a type that already has requests cannot be
 * deleted nor lose fields; it can only be deactivated.
 *
 * @since 1.0.0
 */
@Service
public class RequestTypeCommandServiceImpl implements RequestTypeCommandService {
    private final RequestTypeRepository requestTypeRepository;
    private final RequestRepository requestRepository;

    /**
     * Constructor.
     *
     * @param requestTypeRepository the {@link RequestTypeRepository} instance
     * @param requestRepository the {@link RequestRepository} instance
     */
    public RequestTypeCommandServiceImpl(RequestTypeRepository requestTypeRepository,
                                         RequestRepository requestRepository) {
        this.requestTypeRepository = requestTypeRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(CreateRequestTypeCommand command) {
        if (requestTypeRepository.existsByName(command.name().trim()))
            return Result.failure(ApplicationError.conflict("RequestType", "Name '%s' already exists".formatted(command.name())));
        try {
            var requestType = new RequestType(command.name(), command.description(),
                    command.requiresAttachment(), command.balanceDeduction());
            command.fields().forEach(requestType::addField);
            return Result.success(requestTypeRepository.save(requestType).getId());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("request-type", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-request-type", e.getMessage()));
        }
    }

    @Override
    public Result<RequestType, ApplicationError> handle(UpdateRequestTypeCommand command) {
        if (requestTypeRepository.existsByNameAndIdIsNot(command.name().trim(), command.requestTypeId()))
            return Result.failure(ApplicationError.conflict("RequestType", "Name '%s' already exists".formatted(command.name())));
        return execute(command.requestTypeId(), "update-request-type",
                requestType -> requestType.update(command.name(), command.description(),
                        command.requiresAttachment(), command.balanceDeduction()));
    }

    @Override
    public Result<RequestType, ApplicationError> handle(AddRequestFieldCommand command) {
        return execute(command.requestTypeId(), "add-request-field",
                requestType -> requestType.addField(new RequestField(new FieldKey(command.key()), command.label(),
                        command.dataType(), command.required(), command.displayOrder())));
    }

    @Override
    public Result<RequestType, ApplicationError> handle(RemoveRequestFieldCommand command) {
        if (requestRepository.existsByRequestTypeId(command.requestTypeId()))
            return Result.failure(ApplicationError.businessRuleViolation("request-field-removal",
                    "A request type with requests cannot lose fields; create a new type or deactivate it instead"));
        return execute(command.requestTypeId(), "remove-request-field",
                requestType -> requestType.removeField(command.fieldId()));
    }

    @Override
    public Result<RequestType, ApplicationError> handle(ActivateRequestTypeCommand command) {
        return execute(command.requestTypeId(), "activate-request-type", RequestType::activate);
    }

    @Override
    public Result<RequestType, ApplicationError> handle(DeactivateRequestTypeCommand command) {
        return execute(command.requestTypeId(), "deactivate-request-type", RequestType::deactivate);
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteRequestTypeCommand command) {
        if (requestTypeRepository.findById(command.requestTypeId()).isEmpty())
            return Result.failure(ApplicationError.notFound("RequestType", command.requestTypeId().toString()));
        if (requestRepository.existsByRequestTypeId(command.requestTypeId()))
            return Result.failure(ApplicationError.businessRuleViolation("request-type-deletion",
                    "A request type with requests cannot be deleted; deactivate it instead"));
        try {
            requestTypeRepository.deleteById(command.requestTypeId());
            return Result.success(command.requestTypeId());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("delete-request-type", e.getMessage()));
        }
    }

    /**
     * Loads the request type, applies the change and saves it.
     *
     * @param requestTypeId the request type id
     * @param action        the name of the action, used in the error messages
     * @param change        the change to apply
     * @return the updated request type or an application error
     */
    private Result<RequestType, ApplicationError> execute(Long requestTypeId, String action, Consumer<RequestType> change) {
        var result = requestTypeRepository.findById(requestTypeId);
        if (result.isEmpty())
            return Result.failure(ApplicationError.notFound("RequestType", requestTypeId.toString()));
        var requestType = result.get();
        try {
            change.accept(requestType);
            return Result.success(requestTypeRepository.save(requestType));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("request-type", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation(action, e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected(action, e.getMessage()));
        }
    }
}
