package aplicacionesmoviles.avanzado.todosalau.ejemplopodometro;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView stepCountTextView;
    private TextView accelerometerValuesTextView;
    private Button startButton;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isCountingSteps = false;
    private int stepCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias a las vistas en el diseño
        stepCountTextView = findViewById(R.id.stepCountTextView);
        startButton = findViewById(R.id.startButton);
        accelerometerValuesTextView = findViewById(R.id.accelerometerValuesTextView);

        // Inicialización del SensorManager y del acelerómetro
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Listener para el botón de inicio
        startButton.setOnClickListener(v -> {
            if (!isCountingSteps) {
                // Comienza a contar pasos
                isCountingSteps = true;
                startButton.setText("Stop");
                stepCount = 0;
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                // Detiene el conteo de pasos
                isCountingSteps = false;
                startButton.setText("Start");
                sensorManager.unregisterListener(this);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Obtiene los valores del acelerómetro
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            // Calcula la magnitud del vector de aceleración
            double magnitude = Math.sqrt(x * x + y * y + z * z);

            // Comprueba si se ha dado un paso basado en la magnitud del vector de aceleración
            if (magnitude > 10) { // Ajusta este umbral según sea necesario
                stepCount++;
                stepCountTextView.setText("Step Count: " + stepCount);
            }

            // Actualiza la vista con los valores del acelerómetro
            accelerometerValuesTextView.setText("Accelerometer Values: \n X = " + x + ", \n Y = " + y + ", \n Z = " + z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se utiliza
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pausa el conteo de pasos si está en progreso
        if (isCountingSteps) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reanuda el conteo de pasos si estaba en progreso
        if (isCountingSteps) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
}