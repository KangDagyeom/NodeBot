module com.javaee.test1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.net.http;

    opens com.javaee.test1 to javafx.fxml;
    exports com.javaee.test1;
}
