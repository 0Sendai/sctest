package ru.sctest.backend.assignment.assignmentResult;

import java.util.NoSuchElementException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.sctest.backend.assignment.assignmentResult.dto.PostRequest;
import ru.sctest.backend.db.model.AssignmentResult;
import ru.sctest.backend.db.model.AssignmentResultStatus;
import ru.sctest.backend.db.repo.AssignmentResultRepository;

@Service 
public class AssignmentResultUpdateService {

    @Autowired 
    private AssignmentResultRepository assignmentResultRepository;

    // TODO student secure
    public Boolean updateAssignmentResult(PostRequest requestBody) throws Exception {
        Long assignResId = requestBody.resultId();
        if (assignResId == null) throw new Exception("resultId is null");
        AssignmentResult assignmentResult = assignmentResultRepository
            .findById(assignResId)
            .orElseThrow(() -> new NoSuchElementException("Can't find assignmentResult"));
        
        String statusStr = requestBody.status();
        if (statusStr == null || statusStr.isBlank()) 
            throw new Exception("empty status");
        AssignmentResultStatus status;
        try {
            status = AssignmentResultStatus.valueOf(statusStr);
        } catch (Exception e) {
            throw new Exception("illegal status");
        }
        // TODO set current slide
        assignmentResult.setStatus(status);
        assignmentResult.setCompletedAt(LocalDateTime.now());
        assignmentResultRepository.save(assignmentResult);

        return true;
    }
}
