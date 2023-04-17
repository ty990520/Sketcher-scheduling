package sketcher.scheduling.repository.assignSchedule.buikInsert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sketcher.scheduling.domain.ManagerAssignSchedule;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;


@Repository
public class AssignScheduleBulkInsertRepositoryImpl implements AssignScheduleBulkInsertRepository {

    @Autowired
    private DataSource dataSource;

    @Override
    public void assignScheduleBulkInsertWithBatchSize100(List<ManagerAssignSchedule> entities) {
        PreparedStatement pstmt = null;
        Connection con = null;

        try {
            String sql = "INSERT INTO manager_assign_schedule ( update_req_id, schedule_date_time_start, schedule_date_time_end, user_code) VALUES (?, ?, ?, ?)";

            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql);


            for (int i = 0; i < entities.size(); i++) {
                ManagerAssignSchedule entity = entities.get(i);

                pstmt.setNull(1, java.sql.Types.NULL);
                pstmt.setTimestamp(2, Timestamp.valueOf(entity.getScheduleDateTimeStart()));
                pstmt.setTimestamp(3, Timestamp.valueOf(entity.getScheduleDateTimeEnd()));
                pstmt.setObject(4, entity.getUser().getCode());

                pstmt.addBatch();
                pstmt.clearParameters();

                if ((entities.size() % 100) == 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                    con.commit();
                }

            }
            pstmt.executeBatch();
            con.commit();
        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }

    }
}