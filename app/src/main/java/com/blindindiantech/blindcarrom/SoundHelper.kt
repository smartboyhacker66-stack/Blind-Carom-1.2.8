vpackage com.blindindiantech.blindcarrom  
​import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin  
​data class Tone(val freq: Double, val ms: Int, val vol: Float = 1f)  
​object SoundHelper {
private const val RATE = 22050  
​fun play(vararg tones: Tone) {
Thread {
for (t in tones) playOne(t)
}.start()
}  
​private fun playOne(t: Tone) {
try {
val n = RATE * t.ms / 1000
val buf = ShortArray(n)
val fade = RATE / 200
for (i in 0 until n) {
var env = 1.0
if (i < fade) env = i.toDouble() / fade
if (i > n - fade) env = (n - i).toDouble() / fade
val v = sin(2 * PI * t.freq * i / RATE) * env * t.vol * 0.8
buf[i] = (v * Short.MAX_VALUE).toInt().toShort()
}
val track = AudioTrack.Builder()
.setAudioAttributes(
AudioAttributes.Builder()
.setUsage(AudioAttributes.USAGE_GAME)
.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
.build()
)
.setAudioFormat(
AudioFormat.Builder()
.setEncoding(AudioFormat.ENCODING_PCM_16BIT)
.setSampleRate(RATE)
.setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
.build()
)
.setBufferSizeInBytes(n * 2)
.setTransferMode(AudioTrack.MODE_STATIC)
.build()
track.write(buf, 0, n)
track.play()
Thread.sleep(t.ms.toLong() + 40)
track.release()
} catch (e: Exception) {
}
}
}
