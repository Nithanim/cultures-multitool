package me.nithanim.cultures.format.bmd;

import java.util.List;
import lombok.ToString;
import lombok.ToString.Exclude;
import lombok.Value;
import me.nithanim.cultures.format.bmd.RawBmdFileReader.BmdFrameInfo;
import me.nithanim.cultures.format.bmd.RawBmdFileReader.BmdFrameRow;
import me.nithanim.cultures.format.bmd.RawBmdFileReader.BmdHeader;

@Value
public class RawBmdFile {
  BmdHeader header;
  List<BmdFrameInfo> frameInfo;
  @ToString.Exclude
  byte[] pixels;
  @ToString.Exclude
  List<BmdFrameRow> rowInfo;
}
