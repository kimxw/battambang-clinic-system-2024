module com.orb.battambang {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.orb.battambang to javafx.fxml;
    exports com.orb.battambang;
    exports com.orb.battambang.login;
    opens com.orb.battambang.login to javafx.fxml;
    exports com.orb.battambang.connection;
    opens com.orb.battambang.connection to javafx.fxml;
    exports com.orb.battambang.util;
    opens com.orb.battambang.util to javafx.fxml;
}