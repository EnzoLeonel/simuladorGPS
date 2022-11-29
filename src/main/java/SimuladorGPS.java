import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

public class SimuladorGPS implements Runnable{
    //Variables para la generacion del reporte
    private double latitude = 0;
    private double longitude = 0;
    private double gpsDop = 0;
    private String reportType = "";
    private String loginCode = "";
    private String reportDate = "";
    private int heading = 0;
    private double speed = 0;
    private String speedLabel = "";
    private int gpsSatellites = 0;
    private String text = "";
    private String textLabel = "";
    private final String URL;

    //Variables para generacion de header HTTP
    private long unixTimestamp;
    private String hash;
    private final String APPLICATIONID;
    private final String SECRETKEY;

    //Variables para peticion y respuesta HTTP
    private CloseableHttpClient httpclient;
    private HttpUriRequest request;
    private HttpResponse response;

    //Variables de entorno de ejecucion de simulador, con valores por defecto por si no se especifican
    private int timeOut = 10;                   //Cantidad en minutos de espera satisfactoria de servidor
    private boolean infiniteLoop = false;       //Si es true, ejecuta el simulador indefinidamente
    private int maxSpeed = 80;                  //Velocidad maxima que genera el simulador expresada en KM/H
    private boolean randomGeneration = false;   //Si es true, genera automaticamente datos aleatorios
    private int interval = 60;                  //Intervalo de tiempo usado para enviar los reportes, expresado en segundos

    //Constructor para definir variables constantes y obligatorias
    public SimuladorGPS(String url, String applicationID, String secretKey) {
        URL = url;
        APPLICATIONID = applicationID;
        SECRETKEY = secretKey;
    }

    //Setters & Getters
    public void setGpsDop(double gpsDop) {
        this.gpsDop = gpsDop;
    }
    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }
    public void setGpsSatellites(int gpsSatellites) {
        this.gpsSatellites = gpsSatellites;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }
    public void setHeading(int heading) {
        this.heading = heading;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public void setSpeedLabel(String speedLabel) {
        this.speedLabel = speedLabel;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setTextLabel(String textLabel) {
        this.textLabel = textLabel;
    }
    public void setInfiniteLoop(boolean infiniteLoop){ this.infiniteLoop = infiniteLoop; }
    public void setTimeout(int timeOut){ this.timeOut = timeOut; }
    public void setRandomGeneration(boolean randomGeneration) { this.randomGeneration = randomGeneration; }
    public void setInterval(int interval) { this.interval = interval; }
    public HttpResponse getResponse() { return response; }

    //Metodo para actualizar hashCode
    private void updateHashCode() {
        unixTimestamp = getUnixTimestamp();
        byte [] md5 = DigestUtils.md5(APPLICATIONID + SECRETKEY + unixTimestamp);
        hash = Base64.getEncoder().encodeToString(md5);
    }
    private long getUnixTimestamp() {
        return Instant.now().getEpochSecond();
    }

    private void generateData() {
        latitude = (Math.random() * (180)) - 90;
        longitude = (Math.random() * (180)) - 90;
        gpsSatellites = (int) Math.round(Math.random() * 3) + 1;
        heading = (int) Math.round(Math.random() * 360);
        speed = Math.random() * maxSpeed;
        gpsDop = Math.random() * 21;
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        reportDate = df.format(new Date());
    }

    //Funcion encargada de generar body de la peticion HTTP
    private String dataToStringJson() {
        String json = "{" +
                "\"loginCode\":\"" + loginCode + "\"," +
                "\"reportDate\":\"" + reportDate + "\"," +
                "\"reportType\":\"" + reportType + "\"," +
                "\"latitude\":" + String.format("%.6f",latitude).replace(",",".") + "," +
                "\"longitude\":" + String.format("%.6f",longitude).replace(",",".") + "," +
                "\"gpsDop\":" + String.format("%.1f",gpsDop).replace(",",".") + "," +
                "\"gpsSatellites\":" + gpsSatellites + "," +
                "\"heading\":" + heading + "," +
                "\"speed\":" + String.format("%.1f",speed).replace(",",".") + "," +
                "\"speedLabel\":\"" + speedLabel + "\"," +
                "\"text\":\"" + text + "\"," +
                "\"textLabel\":\"" + textLabel + "\"" +
                "}";
        return json;
    }

    //Metodo para enviar peticion con timestamps y hash actualizados, devuelve status HTTP
    public int sendUpdateData() throws IOException {
        updateHashCode();
        httpclient = HttpClients.createDefault();
        request = RequestBuilder.put()
                .setUri(URL)
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setHeader("Authorization", "SWSAuth application=\"ReportGeneratorTest\",signature=\""+hash+"\",timestamp=\""+unixTimestamp+"\"")
                .setEntity(new StringEntity(dataToStringJson()))
                .build();
        response = httpclient.execute(request);
        return response.getStatusLine().getStatusCode();
    }

    //Metodo para enviar ultima peticion creada, sin cambiar datos
    public int sendLastData() throws IOException {
        httpclient = HttpClients.createDefault();
        response = httpclient.execute(request);
        return response.getStatusLine().getStatusCode();
    }

    @Override
    public void run() {
        try {

            do {
                if (randomGeneration) { generateData(); }
                int statusCode = sendUpdateData();
                //Si el codigo de respuesta es 429 o mayor a 500 y no han transcurrido 10 minutos desde la generacion del reporte
                while ((statusCode == 429 || statusCode >= 500) && unixTimestamp + 20 >= getUnixTimestamp()) {
                    System.out.println("Envio fallido, reintentando...\n" + EntityUtils.toString(response.getEntity()));
                    Thread.sleep(10000);
                    statusCode = sendLastData();
                }
                if (statusCode == 200) {
                    System.out.println("\nReporte enviado correctamente:\n" +
                            "Request: " + dataToStringJson() +
                            "\nResponse: " + EntityUtils.toString(response.getEntity()));
                }
                if (infiniteLoop) {
                    System.out.println("\n Se enviará proximo reporte en " + interval + " segundos...");
                    Thread.sleep(interval * 1000);
                }
            } while(infiniteLoop);

        } catch (InterruptedException | IOException e) {
            System.out.println(e);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
