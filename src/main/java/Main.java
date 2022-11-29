
public class Main {

    public static void main(String[] args) {
        //Variables usadas para cambiar los datos de la aplicacion mas facilmente
        String url = "https://test-externalrgw.ar.sitrack.com/frame";
        String applicationID = "ReportGeneratorTest";
        String secretKey = "ccd517a1-d39d-4cf6-af65-28d65e192149";
        String loginCode = "98173";
        String text = "Enzo Sanchez";
        String textLabel = "TAG";
        String reportType = "2";
        String speedLabel = "GPS";

        try {
            //Instancia simulador con datos y configuraciones del usuario (Ver clase SimuladorGPS)
            SimuladorGPS simulador = new GPSBuilder(url, applicationID, secretKey)
                    .setInfiniteLoop(true)
                    .setTimeOut(10)
                    .setRandomGeneration(true)
                    .setInterval(60)
                    .setText(text)
                    .setTextLabel(textLabel)
                    .setLoginCode(loginCode)
                    .setReportType(reportType)
                    .setSpeedLabel(speedLabel)
                    .build();

            //Ejecuta el simulador de forma Síncrona.
            //Para ejecutar de forma asíncronca, crear instancia Thread con simulador
            simulador.run();

        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("\n- Simulador de GPS de Enzo Leonel Sanchez -\n");
    }
}
