package es.leinadfonfria.eyteacher.infrastructure.services;

import es.leinadfonfria.eyteacher.application.dtos.correction.CorrectionResponse;
import es.leinadfonfria.eyteacher.application.dtos.correction.CorrectionResponseMapper;
import es.leinadfonfria.eyteacher.application.services.correction.AddCorrectionRequest;
import es.leinadfonfria.eyteacher.application.services.correction.AddCorrectionUseCase;
import es.leinadfonfria.eyteacher.application.services.correction.GetCorrectionBySolutionIdUseCase;
import es.leinadfonfria.eyteacher.application.shared.Result;
import es.leinadfonfria.eyteacher.domain.entities.Correction;
import es.leinadfonfria.eyteacher.domain.entities.User;
import es.leinadfonfria.eyteacher.domain.errors.AuthException;
import es.leinadfonfria.eyteacher.domain.errors.ErrorCode;
import es.leinadfonfria.eyteacher.domain.errors.NotFoundException;
import es.leinadfonfria.eyteacher.domain.ports.CorrectionRepository;
import es.leinadfonfria.eyteacher.domain.ports.UserRepository;
import es.leinadfonfria.eyteacher.infrastructure.events.NotificationPublisher;
import es.leinadfonfria.eyteacher.infrastructure.events.messages.NewCorrectionMessage;
import es.leinadfonfria.eyteacher.infrastructure.security.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class CorrectionServiceImpl implements AddCorrectionUseCase, GetCorrectionBySolutionIdUseCase {
    private final CorrectionRepository<Correction> correctionRepository;
    private final UserRepository<User> userRepository;
    private final CorrectionResponseMapper correctionResponseMapper;
    private final NotificationPublisher notificationPublisher;

    /**
     * Adds a new correction to a solution (Only Teacher Role).
     *
     * @param request The correction details.
     * @return Result containing the correction ID or an error code.
     */
    @Override
    @Transactional
    public Result<Long, Integer> addCorrection(AddCorrectionRequest request) {
        try {
            if (!AuthenticationUtils.isTeacher()) {
                throw new AuthException("User is not a TEACHER", ErrorCode.USER_NOT_TEACHER);
            }
            UUID teacherId = AuthenticationUtils.getUserId();
            User teacher = userRepository.findById(teacherId)
                    .orElseThrow(() -> new AuthException("Teacher not found", ErrorCode.USER_NOT_FOUND));
            Correction correction = Correction.create(request.description(), teacher, request.solutionId());
            Correction saved = correctionRepository.save(correction, request.solutionId());
            notificationPublisher.publishNewCorrection(new NewCorrectionMessage(
                    saved.getId(),
                    teacher.getFullName(),
                    request.solutionId()
            ));
            return Result.ok(saved.getId());
        } catch (AuthException e) {
            log.error("Authentication error during correction addition", e);
            return Result.fail(e.getCode());
        } catch (NotFoundException e) {
            log.error("Not found error during correction addition", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during correction addition", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Retrieves a correction by solution ID.
     *
     * @param solutionId The ID of the solution.
     * @return Result containing the CorrectionResponse or an error code.
     */
    @Override
    public Result<CorrectionResponse, Integer> getCorrectionBySolutionId(Long solutionId) {
        try {
            Correction correction = correctionRepository.findBySolutionId(solutionId)
                    .orElseThrow(() -> new NotFoundException("Correction not found", ErrorCode.CORRECTION_NOT_FOUND));
            return Result.ok(correctionResponseMapper.toCorrectionResponse(correction));
        } catch (NotFoundException e) {
            log.error("Not found error during correction retrieval", e);
            return Result.fail(e.getCode());
        } catch (Exception e) {
            log.error("Unexpected error during correction retrieval", e);
            return Result.fail(ErrorCode.UNKNOWN_ERROR);
        }
    }
}
