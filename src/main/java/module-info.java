module com.javaee.test1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.net.http;
    requires java.sql;
    requires async.http.client;
    requires jakarta.mail;
    requires dotenv.java;

    opens com.javaee.test1 to javafx.fxml;
    exports com.javaee.test1;
}
