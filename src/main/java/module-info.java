module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires ij;
	requires java.desktop;
    requires org.jfree.jfreechart;

    opens org.example to javafx.fxml;
    exports org.example;
}
