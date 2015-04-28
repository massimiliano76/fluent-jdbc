package org.codejargon.fluentjdbc.internal.query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner
import spock.lang.Specification;

import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

class ParamAssignerTest extends Specification {
    static final def localDateTime = LocalDateTime.of(2015, Month.MARCH, 5, 12, 5)
    static final def localDate = LocalDate.of(2015, Month.MARCH, 5)
    static final def localTime = LocalTime.of(22, 12)
    static final def instant = localDateTime.toInstant(ZoneOffset.MIN)
    static final def javaDate = new java.util.Date(Date.valueOf(localDate).getTime())

    static final def string = "a"
    static final def longParam = 5L
    static final def intParam = 123;
    static final def bigDecimal = BigDecimal.TEN
    static final def sqlDate = java.sql.Date.valueOf(localDate)
    static final def time = Time.valueOf(localTime)
    static final Timestamp timestamp = Timestamp.valueOf(localDateTime)


    def statement = Mock(PreparedStatement)
    def paramAssigner = new ParamAssigner(DefaultParamSetters.setters())

    def "JDBC types"() {
        when:
        paramAssigner.assignParams(
                statement,
                [string, longParam, intParam, bigDecimal, sqlDate, time, timestamp]
        )
        then:
        1 * statement.setObject(1, string)
        1 * statement.setObject(2, longParam)
        1 * statement.setObject(3, intParam)
        1 * statement.setObject(4, bigDecimal)
        1 * statement.setObject(5, sqlDate)
        1 * statement.setObject(6, time)
        1 * statement.setObject(7, timestamp)
    }

    def "Local java.time types"() {
        when:
        paramAssigner.assignParams(
                statement,
                [localDateTime, localDate, localTime]
        )
        then:
        1 * statement.setTimestamp(1, timestamp)
        1 * statement.setDate(2, sqlDate)
        1 * statement.setTime(3, time)
    }

    def "java.time Instant"() throws SQLException {
        when:
        paramAssigner.assignParams(
                statement,
                [instant]
        )
        then:
        1 * statement.setTimestamp(1, Timestamp.from(instant));
    }

    def "java.util Date"() throws SQLException {
        when:
        paramAssigner.assignParams(
                statement,
                [javaDate]
        )
        then:
        1 * statement.setDate(1, sqlDate)
    }

}
