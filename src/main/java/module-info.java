module com.javaee.test1 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.javaee.test1 to javafx.fxml;
    exports com.javaee.test1;
}
