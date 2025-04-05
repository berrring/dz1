module org.example.homework111 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.ooxml;


    opens org.example.homework111 to javafx.fxml;
    exports org.example.homework111;
}