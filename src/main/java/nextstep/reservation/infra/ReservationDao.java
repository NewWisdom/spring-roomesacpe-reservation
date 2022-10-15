package nextstep.reservation.infra;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.sql.DataSource;
import nextstep.reservation.domain.Reservation;
import nextstep.reservation.domain.ReservationRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class ReservationDao implements ReservationRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertReservation;
    private final RowMapper<Reservation> mapper;

    public ReservationDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertReservation = new SimpleJdbcInsert(dataSource)
            .withTableName("reservation")
            .usingGeneratedKeyColumns("id");
        this.mapper = (resultSet, rowNum) -> new Reservation(
            resultSet.getLong("id"),
            resultSet.getLong("schedule_id"),
            resultSet.getDate("date").toLocalDate(),
            resultSet.getTime("time").toLocalTime(),
            resultSet.getString("name")
        );
    }

    @Override
    public Reservation save(Reservation reservation) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(reservation);
        Long id = insertReservation.executeAndReturnKey(parameters).longValue();
        return new Reservation(id, reservation);
    }

    @Override
    public List<Reservation> findAllByDate(LocalDate date) {
        String sql = "SELECT * FROM reservation WHERE date = ?";
        return jdbcTemplate.query(sql, mapper, date);
    }

    @Override
    public int deleteByDateTime(LocalDate date, LocalTime time) {
        String sql = "DELETE FROM reservation WHERE date = ? AND time = ?";
        return jdbcTemplate.update(sql, date, time);
    }

    @Override
    public boolean existsByScheduleId(Long scheduleId) {
        String sql = "SELECT COUNT(*) FROM reservation WHERE schedule_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, scheduleId);
        return count > 0;
    }
}
