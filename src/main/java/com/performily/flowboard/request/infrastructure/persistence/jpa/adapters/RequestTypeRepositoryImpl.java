package com.performily.flowboard.request.infrastructure.persistence.jpa.adapters;

import com.performily.flowboard.request.domain.model.entities.RequestType;
import com.performily.flowboard.request.domain.repositories.RequestTypeRepository;
import com.performily.flowboard.request.infrastructure.persistence.jpa.assemblers.RequestTypePersistenceAssembler;
import com.performily.flowboard.request.infrastructure.persistence.jpa.repositories.RequestTypePersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Request Type Repository Impl
 * @summary
 * Repository adapter that bridges the request type domain repository port with Spring Data JPA.
 *
 * Methods run inside a transaction so the lazy fields of each type can be read
 * while assembling the domain object.
 *
 * @since 1.0.0
 */
@Repository
@Transactional
public class RequestTypeRepositoryImpl implements RequestTypeRepository {
    private final RequestTypePersistenceRepository requestTypePersistenceRepository;

    /**
     * Constructor.
     *
     * @param requestTypePersistenceRepository the {@link RequestTypePersistenceRepository} instance
     */
    public RequestTypeRepositoryImpl(RequestTypePersistenceRepository requestTypePersistenceRepository) {
        this.requestTypePersistenceRepository = requestTypePersistenceRepository;
    }

    @Override
    public Optional<RequestType> findById(Long id) {
        return requestTypePersistenceRepository.findById(id).map(RequestTypePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<RequestType> findAll() {
        return requestTypePersistenceRepository.findAllByOrderByNameAsc().stream()
                .map(RequestTypePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<RequestType> findAllByActiveTrue() {
        return requestTypePersistenceRepository.findAllByActiveTrueOrderByNameAsc().stream()
                .map(RequestTypePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public RequestType save(RequestType requestType) {
        var saved = requestTypePersistenceRepository.saveAndFlush(RequestTypePersistenceAssembler.toPersistenceFromDomain(requestType));
        return RequestTypePersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public void deleteById(Long id) {
        requestTypePersistenceRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return requestTypePersistenceRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdIsNot(String name, Long id) {
        return requestTypePersistenceRepository.existsByNameAndIdIsNot(name, id);
    }
}
