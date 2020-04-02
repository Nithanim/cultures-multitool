module culturestools.gui {
  requires javafx.base;
  requires javafx.fxml;
  requires javafx.controls;
  requires javafx.graphics;
  requires static lombok;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.ikonli.fontawesome5;
  requires org.apache.commons.io;
  requires culturestools.format.lib;
  requires culturestools.format.cif;
  requires culturestools.format.pcx;
  requires culturestools.format.bmd;
  requires java.desktop;
  requires info.picocli;

  opens me.nithanim.cultures.multitool to
      javafx.fxml,
      javafx.graphics,
      info.picocli;
}
