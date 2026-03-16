package test.repository;

import org.junit.Assert;
import org.junit.Test;
import ru.kocha.exchanger_v1.repository.connection.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataSourceTest {

//    @Test
//    public void testConnection() throws SQLException {
//        try (Connection connection = DataSource.getConnection()) {
//            Assert.assertNotNull("Соединение не должно быть null",connection);
//            Assert.assertFalse("Соединение не должно быть закрыто", connection.isClosed());
//
//            try (Statement statement = connection.createStatement()) {
//                boolean executed = statement.execute("SELECT 1");
//                Assert.assertTrue("Запрос должен выполниться", executed);
//            }
//        }
//    }
//
//    @Test
//    public void testMultiplyConnection() throws SQLException {
//        try (Connection connection = DataSource.getConnection();
//             Connection connection2 = DataSource.getConnection()) {
//            Assert.assertNotNull(connection);
//            Assert.assertNotNull(connection2);
//            Assert.assertNotSame("Должны быть разные соединения",connection,connection2);
//        }
//    }
}
