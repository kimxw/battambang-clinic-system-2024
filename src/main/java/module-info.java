module com.orb.battambang {
    requires javafx.controls;
    requires javafx.fxml;
            
    requires org.controlsfx.controls;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.orb.battambang to javafx.fxml;
    exports com.orb.battambang;

    opens com.orb.battambang.login to javafx.fxml;
    exports com.orb.battambang.login;

    opens com.orb.battambang.connection to javafx.fxml;
    exports com.orb.battambang.connection;

    opens com.orb.battambang.util to javafx.fxml;
    exports com.orb.battambang.util;

    opens com.orb.battambang.reception to javafx.fxml, javafx.base;
    exports com.orb.battambang.reception;

    opens com.orb.battambang.checkupstation to javafx.fxml;

    opens com.orb.battambang.doctor to javafx.fxml;

    opens com.orb.battambang.pharmacy to javafx.fxml;
}