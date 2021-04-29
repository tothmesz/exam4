package hu.nive.ujratervezes.jurassic;

import cccr.CCCRTestExecutionListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({CCCRTestExecutionListener.class})
class JurassicParkTest {

    private static final String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private JurassicPark jurassicPark;

    @BeforeEach
    void init() throws SQLException {
        jurassicPark = new JurassicPark(DB_URL, DB_USER, DB_PASSWORD);
        createTable();
    }

    @AfterEach
    void destruct() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String dropTable = "DROP TABLE IF EXISTS dinosaur";
            Statement statement = connection.createStatement();
            statement.execute(dropTable);
        }
    }

    @Test
    void test_emptyDatabase() {
        assertEquals(List.of(), jurassicPark.checkOverpopulation());
    }

    @Test
    void test_notOverpopulated() throws SQLException {
        insertNotOverpopulated();
        assertEquals(List.of(), jurassicPark.checkOverpopulation());
    }

    @Test
    void test_justVelociraptor() throws SQLException {
        insertVelociraptor();
        assertEquals(List.of("Velociraptor"), jurassicPark.checkOverpopulation());
    }

    @Test
    void test_singleOverpopulated() throws SQLException {
        insertNotOverpopulated();
        insertVelociraptor();
        assertEquals(List.of("Velociraptor"), jurassicPark.checkOverpopulation());
    }

    @Test
    void test_multipleOverpopulated() throws SQLException {
        insertOverpopulated();
        insertNotOverpopulated();
        insertVelociraptor();
        assertEquals(List.of("Hypsilophodontida", "Maiasaurus", "Velociraptor"), jurassicPark.checkOverpopulation());
    }

    private void createTable() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String createTable = "CREATE TABLE IF NOT EXISTS dinosaur (" +
                    "breed VARCHAR(255) PRIMARY KEY, " +
                    "expected INT, " +
                    "actual INT" +
                    ");";
            Statement statement = connection.createStatement();
            statement.execute(createTable);
        }
    }

    private void insertNotOverpopulated() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertNotOverpopulated = "INSERT INTO dinosaur (breed, expected, actual) VALUES " +
                    "('Tyrannosaurus Rex', 2, 2)," +
                    "('Stegosaurus', 4, 4);";
            Statement statement = connection.createStatement();
            statement.execute(insertNotOverpopulated);
        }
    }

    private void insertOverpopulated() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertOverpopulated = "INSERT INTO dinosaur (breed, expected, actual) VALUES " +
                    "('Maiasaurus', 21, 22)," +
                    "('Hypsilophodontida', 33, 34);";
            Statement statement = connection.createStatement();
            statement.execute(insertOverpopulated);
        }
    }

    private void insertVelociraptor() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertVelociraptor = "INSERT INTO dinosaur (breed, expected, actual) VALUES " +
                    "('Velociraptor', 8, 37);";
            Statement statement = connection.createStatement();
            statement.execute(insertVelociraptor);
        }
    }

}