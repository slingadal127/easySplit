module EasySplit {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;

    opens application to javafx.fxml;
    exports application;
}
