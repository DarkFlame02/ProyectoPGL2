package com.example.proyectopgl2.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopgl2.R
import com.example.proyectopgl2.databinding.ActivityGrabadoraBinding
import java.io.IOException
import kotlin.system.exitProcess

class GrabadoraActivity : AppCompatActivity() {
    // ViewBinding para acceder a los elementos de la interfaz de usuario
    private lateinit var binding: ActivityGrabadoraBinding
    // Instancias de MediaRecorder y MediaPlayer para grabar y reproducir audio
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    // Bandera para saber si estamos grabando
    private var isRecording = false
    private var playing = false
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
        binding = ActivityGrabadoraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_info -> {
                    mostrarInformacionApp()
                }
                R.id.nav_logout -> {
                    logout()
                }
                R.id.nav_exit -> {
                    mostrarConfirmacionSalida()
                }
            }
            true
        }

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
            if (fileName != null && !playing) {
                // Si existe un archivo grabado, lo reproducimos
                startPlaying()
            } else {
                stopPlaying()
                // Si no hay archivo grabado, mostramos un mensaje de error
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.grabadora -> {
                Toast.makeText(this, "Usted se encuentra en grabadora.", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.sensores -> {
                startActivity(Intent(this, SensoresActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
                playing = true // Marcamos como reproduciendo
                binding.btnPlay.setImageResource(android.R.drawable.ic_media_pause)
                setOnCompletionListener {
                    // Al finalizar la reproducción, cambiamos el icono a play
                    playing = false
                    binding.btnPlay.setImageResource(android.R.drawable.ic_media_play)
                    Toast.makeText(this@GrabadoraActivity, "Reproducción terminada", Toast.LENGTH_SHORT).show()
                }
                // Mostramos un mensaje indicando que el audio está reproduciéndose
                Toast.makeText(this@GrabadoraActivity, "Reproduciendo audio...", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                // Si ocurre un error al reproducir el audio, lo mostramos
                e.printStackTrace()
                Toast.makeText(this@GrabadoraActivity, "Error al reproducir audio.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun stopPlaying() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        playing = false // Marcamos como no reproduciendo
        binding.btnPlay.setImageResource(android.R.drawable.ic_media_play)
        Toast.makeText(this@GrabadoraActivity, "Reproducción detenida", Toast.LENGTH_SHORT).show()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.playbackParams = it.playbackParams.setSpeed(speed)
                }
            }
        } else {
            Toast.makeText(this, "Cambio de velocidad no soportado en esta versión de Android.", Toast.LENGTH_SHORT).show()
        }
    }

    // Método que se ejecuta cuando la actividad se detiene o sale de la pantalla
    override fun onStop() {
        super.onStop()
        mediaRecorder?.release()// Liberamos los recursos del MediaRecorder y el MediaPlayer para evitar fugas de memoria
        mediaPlayer?.release()
    }
    // Muestra el cuadro de dialogo con informacion de la aplicacion
    private fun mostrarInformacionApp() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.informacion))
        builder.setMessage(getString(R.string.descripcion_app))
        builder.setPositiveButton(getString(R.string.cerrar)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
    // Cierra la sesion del usuario
    private fun logout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.cerrar_sesion))
        builder.setMessage(getString(R.string.estas_seguro_que_deseas_cerrar_sesion))
        builder.setPositiveButton(getString(R.string.si)) { dialog, _ ->
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, getString(R.string.sesion_cerrada), Toast.LENGTH_SHORT).show()
            finish()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
    // Muestra el cuadro de dialogo para confirmar la salida de la aplicacion
    private fun mostrarConfirmacionSalida() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.confirmar_salida))
        builder.setMessage(getString(R.string.estas_seguro_que_deseas_salir))
        builder.setPositiveButton(getString(R.string.si)) { dialog, _ ->
            finishAffinity()
            exitProcess(0)
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

}