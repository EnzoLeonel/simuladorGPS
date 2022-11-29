public class GPSBuilder {
    private SimuladorGPS simulador;
    //Se define builder con campos obligatorios para poder funcionar el simulador
    public GPSBuilder(String url, String applicationID, String secretKey) {
        simulador = new SimuladorGPS(url, applicationID, secretKey);
    }

    public GPSBuilder setGpsDop(double gpsDop) {
        simulador.setGpsDop(gpsDop);
        return this;
    }
    public GPSBuilder setLoginCode(String loginCode) {
        simulador.setLoginCode(loginCode);
        return this;
    }
    public GPSBuilder setGpsSatellites(int gpsSatellites) {
        simulador.setGpsSatellites(gpsSatellites);
        return this;
    }
    public GPSBuilder setLatitude(double latitude) {
        simulador.setLatitude(latitude);
        return this;
    }
    public GPSBuilder setLongitude(double longitude) {
        simulador.setLongitude(longitude);
        return this;
    }
    public GPSBuilder setReportType(String reportType) {
        simulador.setReportType(reportType);
        return this;
    }
    public GPSBuilder setReportDate(String reportDate) {
        simulador.setReportDate(reportDate);
        return this;
    }
    public GPSBuilder setHeading(int heading) {
        simulador.setHeading(heading);
        return this;
    }
    public GPSBuilder setSpeed(double speed) {
        simulador.setSpeed(speed);
        return this;
    }
    public GPSBuilder setSpeedLabel(String speedLabel) {
        simulador.setSpeedLabel(speedLabel);
        return this;
    }
    public GPSBuilder setText(String text) {
        simulador.setText(text);
        return this;
    }
    public GPSBuilder setTextLabel(String textLabel) {
        simulador.setTextLabel(textLabel);
        return this;
    }
    public GPSBuilder setInfiniteLoop(boolean infiniteLoop) {
        simulador.setInfiniteLoop(infiniteLoop);
        return this;
    }
    public GPSBuilder setTimeOut(int timeOut) {
        simulador.setTimeout(timeOut);
        return this;
    }
    public GPSBuilder setRandomGeneration(boolean randomGeneration) {
        simulador.setRandomGeneration(randomGeneration);
        return this;
    }
    public GPSBuilder setInterval(int interval) {
        simulador.setInterval(interval);
        return this;
    }

    public SimuladorGPS build() {
        return simulador;
    }
}
