package sketcher.scheduling.repository.assignSchedule.buikInsert;

import sketcher.scheduling.domain.ManagerAssignSchedule;

import java.util.List;

public interface AssignScheduleBulkInsertRepository {
    void assignScheduleBulkInsertWithBatchSize100(List<ManagerAssignSchedule> entities);
}
