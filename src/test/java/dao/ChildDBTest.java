package dao;

import model.Child;
import org.junit.jupiter.api.*;
import utils.DBUtil;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Child Database Operations Tests")
class ChildDBTest {

    private ChildDB db;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        new DBUtil().executeFile("init.sql");
        db = new ChildDB();
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement()) {
            st.execute("TRUNCATE TABLE child CASCADE");
        }
        db.close();
    }

    @Test
    @DisplayName("Should add a child and return it with an ID")
    void addShouldAddChildAndReturnWithId() throws SQLException {
        String firstName = "John";
        String lastName = "Doe";
        LocalDate birthDate = LocalDate.of(2010, 1, 1);
        Child child = new Child(firstName, lastName, birthDate);

        Child addedChild = db.addChild(child);
        System.out.println("[DEBUG_LOG] Added child ID: " + addedChild.id());

        assertNotNull(addedChild.id(), "Child ID should not be null");
        assertEquals(firstName, addedChild.firstName(), "First name should match");
        assertEquals(lastName, addedChild.lastName(), "Last name should match");
        assertEquals(birthDate, addedChild.birthDate(), "Birth date should match");
    }

    @Test
    @DisplayName("Should add a child with null birth date")
    void addShouldHandleNullBirthDate() throws SQLException {
        String firstName = "John";
        String lastName = "Rock";
        Child child = new Child(firstName, lastName, null);

        Child addedChild = db.addChild(child);
        System.out.println("[DEBUG_LOG] Added with null birth: " + addedChild.id());

        assertNotNull(addedChild.id(), "Child ID should not be null");
        assertEquals(firstName, addedChild.firstName(), "First name should match");
        assertEquals(lastName, addedChild.lastName(), "Last name should match");
        assertNull(addedChild.birthDate(), "Birth date should be null");
    }

    @Test
    @DisplayName("Should update an existing child")
    void updateShouldUpdateExistingChild() throws SQLException {
        Child added = db.addChild(new Child("John", "Doe", LocalDate.of(2010, 1, 1)));
        Child updated = new Child(added.id(), "Alice Updated", "Smith Updated", LocalDate.of(2012, 6, 20));

        boolean result = db.updateChild(updated);
        assertTrue(result, "Update operation should return true");

        String query = "SELECT first_name, last_name, birth_date FROM child WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setLong(1, updated.id());
            try (ResultSet rs = pst.executeQuery()) {
                assertTrue(rs.next(), "Updated child should exist in database");
                assertEquals("Alice Updated", rs.getString("first_name"));
                assertEquals("Smith Updated", rs.getString("last_name"));
                assertEquals(LocalDate.of(2012, 6, 20), rs.getDate("birth_date").toLocalDate());
            }
        }
    }

    @Test
    @DisplayName("Should delete an existing child")
    void deleteShouldDeleteExistingChild() throws SQLException {
        Child added = db.addChild(new Child("Alice", "Smith", LocalDate.of(2012, 3, 15)));

        boolean result = db.deleteChild(added.id());
        assertTrue(result, "Child should be deleted");

        String query = "SELECT COUNT(*) FROM child WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setLong(1, added.id());
            try (ResultSet rs = pst.executeQuery()) {
                rs.next();
                assertEquals(0, rs.getInt(1), "Child should not exist after deletion");
            }
        }
    }

    @Test
    @DisplayName("Should return children with at least the specified age")
    void findChildrenWithMinimumAgeShouldReturnChildrenWithMinimumAge() throws SQLException {
        Child child1 = db.addChild(new Child("Ten", "YearsOld", LocalDate.now().minusYears(10)));
        Child child2 = db.addChild(new Child("Five", "YearsOld", LocalDate.now().minusYears(5)));
        Child child3 = db.addChild(new Child("Fifteen", "YearsOld", LocalDate.now().minusYears(15)));

        List<Child> result = db.findChildrenWithMinimumAge(10);
        System.out.println("[DEBUG_LOG] Children with min age 10: " + result.size());

        assertFalse(result.isEmpty(), "Result should not be empty");
        List<Long> resultIds = result.stream().map(Child::id).toList();
        assertTrue(resultIds.contains(child1.id()), "Should contain 10-year-old child");
        assertTrue(resultIds.contains(child3.id()), "Should contain 15-year-old child");
        assertFalse(resultIds.contains(child2.id()), "Should NOT contain 5-year-old child");
    }

    @Test
    @DisplayName("Should return children with null birth date")
    void findChildrenWithoutBirthDateShouldReturnChildrenWithNullBirthDate() throws SQLException {
        Child childWithDate = db.addChild(new Child("With", "Date", LocalDate.of(2010, 5, 5)));
        Child childWithoutDate = db.addChild(new Child("Without", "Date", null));
        System.out.println("[DEBUG_LOG] Child without birth date ID: " + childWithoutDate.id());

        List<Child> result = db.findChildrenWithoutBirthDate();

        assertFalse(result.isEmpty(), "Result should not be empty");
        List<Long> resultIds = result.stream().map(Child::id).toList();
        assertTrue(resultIds.contains(childWithoutDate.id()), "Should contain child without birth date");
        assertFalse(resultIds.contains(childWithDate.id()), "Should NOT contain child with birth date");
        assertTrue(result.stream().allMatch(c -> c.birthDate() == null),
                "All returned children should have null birth date");
    }

    @Test
    @DisplayName("Should return false when deleting non-existent child")
    void deleteShouldReturnFalseForNonExistentChild() throws SQLException {
        Long nonExistentId = 999999L;

        boolean result = db.deleteChild(nonExistentId);

        assertFalse(result, "Deleting non-existent child should return false");
    }

    @Test
    @DisplayName("Should throw exception when adding child with null first name")
    void addShouldThrowExceptionWhenFirstNameIsNull() {
        Child child = new Child(null, "Doe", LocalDate.of(2010, 1, 1));

        assertThrows(IllegalArgumentException.class, () -> db.addChild(child),
                "Should throw IllegalArgumentException when first name is null");
    }

    @Test
    @DisplayName("Should return false when updating non-existent child")
    void updateShouldReturnFalseForNonExistentChild() throws SQLException {
        Child child = new Child(999999L, "Ghost", "Child", LocalDate.of(2010, 1, 1));

        boolean result = db.updateChild(child);

        assertFalse(result, "Updating non-existent child should return false");
    }

    @Test
    @DisplayName("Should return empty list when no children match minimum age")
    void findChildrenWithMinimumAgeShouldReturnEmptyListWhenNoMatch() throws SQLException {
        db.addChild(new Child("Young", "One", LocalDate.now().minusYears(3)));
        db.addChild(new Child("Young", "Two", LocalDate.now().minusYears(5)));

        List<Child> result = db.findChildrenWithMinimumAge(18);

        assertTrue(result.isEmpty(), "Should return empty list when no children match minimum age");
    }
}




