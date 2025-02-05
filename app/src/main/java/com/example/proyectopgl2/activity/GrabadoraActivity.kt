package com.example.proyectopgl2.activity

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.proyectopgl2.R
import com.example.proyectopgl2.databinding.ActivityGrabadoraBinding
import java.io.IOException

class GrabadoraActivity : AppCompatActivity() {

    // ViewBinding para acceder a los elementos de la interfaz de usuario
    private lateinit var binding: ActivityGrabadoraBinding

    // Instancias de MediaRecorder y MediaPlayer para grabar y reproducir audio
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    // Bandera para saber si estamos grabando
    private var isRecording = false

    // Nombre y ubicación del archivo donde se guardará la grabación
    private var fileName: String? = null

    // Constantes para el código de solicitud de permisos y los permisos requeridos
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val permissionsRequired = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflamos la vista usando ViewBinding
        binding = ActivityGrabadoraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Establecemos la ruta y el nombre del archivo donde se grabará el audio
        fileName = "${externalCacheDir?.absolutePath}/grabacion.3gp"

        // Verificamos si tenemos los permisos necesarios para grabar y acceder al almacenamiento
        checkPermissions()

        // Configuramos el botón de grabar
        binding.btnRecord.setOnClickListener {
            if (isRecording) {
                // Si estamos grabando, detenemos la grabación
                stopRecording()
                // Cambiamos el texto del botón a "Grabar"
                binding.btnRecord.text = getString(R.string.rec_txt)
            } else {
                // Si no estamos grabando, comenzamos la grabación
                startRecording()
                // Cambiamos el texto del botón a "Detener"
                binding.btnRecord.text = getString(R.string.stop_txt)
            }
        }

        // Configuramos el botón de reproducir
        binding.btnPlay.setOnClickListener {
            if (fileName != null) {
                // Si existe un archivo grabado, lo reproducimos
                startPlaying()
            } else {
                // Si no hay archivo grabado, mostramos un mensaje de error
                Toast.makeText(this, "No hay archivo de audio grabado.", Toast.LENGTH_SHORT).show()
            }
        }

        // Configuramos el SeekBar para el volumen
        binding.seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setVolume(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Configuramos el SeekBar para la velocidad de reproducción
        binding.seekBarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setPlaybackSpeed(progress / 100f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    // Método para verificar si se tienen los permisos requeridos
    private fun checkPermissions() {
        if (permissionsRequired.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, permissionsRequired, REQUEST_RECORD_AUDIO_PERMISSION)
        }
    }

    // Método para iniciar la grabación de audio
    private fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(fileName)
        }

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            isRecording = true
            Toast.makeText(this, "Grabando...", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al grabar audio: ${e.message}", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Toast.makeText(this, "Error de estado al grabar audio: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para detener la grabación de audio
    private fun stopRecording() {
        mediaRecorder?.apply {
            // Detenemos y liberamos el MediaRecorder
            stop()
            release()
        }
        mediaRecorder = null
        isRecording = false
        // Mostramos un mensaje indicando que la grabación se ha detenido
        Toast.makeText(this, "Grabación detenida", Toast.LENGTH_SHORT).show()
    }

    // Método para comenzar a reproducir la grabación de audio
    private fun startPlaying() {
        // Creamos un objeto MediaPlayer para reproducir el audio
        mediaPlayer = MediaPlayer().apply {
            try {
                // Establecemos el archivo de audio que vamos a reproducir
                setDataSource(fileName)
                // Preparamos el MediaPlayer
                prepare()
                // Comenzamos a reproducir el audio
                start()
                // Mostramos un mensaje indicando que el audio está reproduciéndose
                Toast.makeText(this@GrabadoraActivity, "Reproduciendo audio...", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                // Si ocurre un error al reproducir el audio, lo mostramos
                e.printStackTrace()
                Toast.makeText(this@GrabadoraActivity, "Error al reproducir audio.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para ajustar el volumen del audio
    private fun setVolume(volume: Int) {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val newVolume = (maxVolume * (volume / 100f)).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
    }

    // Método para ajustar la velocidad de reproducción del audio
    private fun setPlaybackSpeed(speed: Float) {
        mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.apply {
            this.speed = speed
        }!!
    }

    // Método que se ejecuta cuando la actividad se detiene o sale de la pantalla
    override fun onStop() {
        super.onStop()
        // Liberamos los recursos del MediaRecorder y el MediaPlayer para evitar fugas de memoria
        mediaRecorder?.release()
        mediaPlayer?.release()
    }
}