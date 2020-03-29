module culturestools {
  requires javafx.base;
  requires javafx.fxml;
  requires javafx.controls;
  requires javafx.graphics;
  requires culturestools.format.lib;
  requires static lombok;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.ikonli.fontawesome5;
  requires org.apache.commons.io;
  requires culturestools.format.cif;
  requires culturestools.format.pcx;
  requires java.desktop;

  opens me.nithanim.cultures.multitool to
      javafx.fxml,
      javafx.graphics;
}
