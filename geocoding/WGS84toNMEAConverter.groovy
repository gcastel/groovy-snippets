import java.math.RoundingMode

def commonConvertWGS84ToNMEA(String coord, String positivePrefix, String negativePrefix) {
  // Split between degrees and decimal part
  String[] splittedCoord = coord.split("\\.")

  if (splittedCoord.length <= 1) {
     throw new IllegalArgumentException("No decimal part for the supplied coordinate")
  }

  StringBuilder nmeaBuilder = new StringBuilder()
  if (Double.valueOf(coord) > 0) {
    nmeaBuilder << positivePrefix
  } else {
    splittedCoord[0] = splittedCoord[0].replace('-', '')
    nmeaBuilder << negativePrefix
  }

  // Extract degrees
  Integer degres = Integer.parseInt(splittedCoord[0])

  // Let's find minutes
  BigDecimal minutes = new BigDecimal("0." + splittedCoord[1])
  minutes = minutes * 60

  String minutesString = minutes.toString()
  // We substract the overflow
  if (minutes >= 100) {
        int hundred = Integer.parseInt(minutesString[0,1])
        degres -= hundred
        minutes -= hundred * 100
  }

  if (degres.toString().length() == 1) {
        nmeaBuilder << "0"
  }
  nmeaBuilder << degres
  nmeaBuilder << ' '
 
  String minutesToPoint = minutes.setScale(4, RoundingMode.HALF_UP)
                .toString()

  if (minutesToPoint.indexOf('.') == 1) {
        minutesToPoint = "0" + minutesToPoint
  }
  nmeaBuilder << minutesToPoint
  nmeaBuilder.toString()
}

def convertWGS84LongitudeToNMEA(String coord) {
  commonConvertWGS84ToNMEA(coord, "E", "W")
}

def convertWGS84LatitudeToNMEA(String coord) {
  commonConvertWGS84ToNMEA(coord, "N", "S")
}


// ==== Test Code ====
assert convertWGS84LatitudeToNMEA("49.3270933333") == "N49 19.6256"
assert convertWGS84LatitudeToNMEA("-49.3270933333") == "S49 19.6256"
assert convertWGS84LongitudeToNMEA("2.6497933333") == "E02 38.9876"
assert convertWGS84LongitudeToNMEA("-2.6497933333") == "W02 38.9876"
