package com.example.player

import android.media.MediaPlayer
import android.util.Log
import com.example.player.bean.SongBean
import com.ssk.ncmusic.core.player.event.PauseSongEvent
import com.ssk.ncmusic.core.player.event.PlaySongEvent
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by ssk on 2022/4/23.
 */
object NCPlayer : IPlayer,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnErrorListener {

    private var mStatus: PlayerStatus = PlayerStatus.IDLE
    var mCurSongBean: SongBean? = null

    val mMediaPlayer = MediaPlayer()

    private val mTimer: Timer = Timer()
    private var mUpdateDuringTask: TimerTask? = null
    private val mListeners = ArrayList<IPlayerListener>()
    private var mJob: Job? = null

    //private val ncApi = EntryPointFinder.getNCApi()

    init {
        mMediaPlayer.setOnCompletionListener(this)
        mMediaPlayer.setOnPreparedListener(this)
        mMediaPlayer.setOnBufferingUpdateListener(this)
        mMediaPlayer.setOnErrorListener(this)
    }

    fun addListener(listener: IPlayerListener) {
        mListeners.add(listener)
    }

    fun removeListener(listener: IPlayerListener) {
        mListeners.remove(listener)
    }

    override fun setDataSource(songBean: SongBean) {
        mCurSongBean = songBean
    }

    override fun start() {
        Log.d("ssk", "start()")
        if (mStatus == PlayerStatus.STARTED
            || mStatus == PlayerStatus.PREPARED
            || mStatus == PlayerStatus.PAUSED
            || mStatus == PlayerStatus.COMPLETED
        ) {
            Log.d("ssk", "start stop ()")
            stop()
        }
        mCurSongBean?.let {
//            MusicPlayService.start()
            getSongUrlAndPlay(it.id)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getSongUrlAndPlay(songId: Long) {
        mJob?.cancel()
        mJob = GlobalScope.launch(context = Dispatchers.IO) {
            try {
                val url = /*ncApi.getSongUrl(songId).data.firstOrNull()?.url
                    ?: */"https://music.163.com/song/media/outer/url?id=$songId.mp3"
                mMediaPlayer.reset()
                mMediaPlayer.setDataSource(url)
                mMediaPlayer.prepareAsync()
                Log.d("ssk", "prepareAsync()")
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("ssk", "getSongUrlAndPlay e = $e")
                    e.printStackTrace()
                    mListeners.forEach {
                        it.onStatusChanged(PlayerStatus.ERROR)
                    }
                }
            }
        }
    }

    override fun pause() {
        if (mStatus == PlayerStatus.STARTED) {
            Log.d("ssk", "pause()")
            mUpdateDuringTask?.cancel()
            setStatus(PlayerStatus.PAUSED)
            mMediaPlayer.pause()
            EventBus.getDefault().post(PauseSongEvent())
        }
    }

    override fun resume() {
        //if (mStatus == PlayerStatus.PAUSED) {
        Log.d("ssk", "resume()")
        innerStartPlay()
        //EventBus.getDefault().post(PlaySongEvent())
        //}
    }

    override fun stop() {
        mUpdateDuringTask?.cancel()
        mMediaPlayer.stop()
        setStatus(PlayerStatus.STOPPED)
        setStatus(PlayerStatus.IDLE)
        Log.d("ssk", "stop()")
    }

    override fun seekTo(position: Int) {
        mMediaPlayer.seekTo(position)
    }

    override fun onCompletion(mp: MediaPlayer) {
        mUpdateDuringTask?.cancel()
        setStatus(PlayerStatus.COMPLETED)
        Log.d("ssk", "onCompletion done")
    }

    override fun onPrepared(mp: MediaPlayer?) {
        setStatus(PlayerStatus.PREPARED)
        //setProgress()
        innerStartPlay()
    }

    private fun innerStartPlay() {
        Log.d("ssk", "innerStartPlay()")
        mMediaPlayer.start()
        setStatus(PlayerStatus.STARTED)
        mCurSongBean?.let {
            EventBus.getDefault().post(PlaySongEvent())
        }
        mUpdateDuringTask?.cancel()
        mUpdateDuringTask = object : TimerTask() {
            override fun run() {
                setProgress()
            }
        }.apply {
            mTimer.schedule(this, 0, 1000)
        }
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.e("ssk", "onError what=${what},extra=${extra}")
        mUpdateDuringTask?.cancel()
        setStatus(PlayerStatus.ERROR)
        setStatus(PlayerStatus.IDLE)
        return true
    }

    private fun setStatus(status: PlayerStatus) {
        mStatus = status
        mListeners.forEach {
            it.onStatusChanged(mStatus)
        }
    }

    private fun setProgress() {
        mListeners.forEach {
            val percentage = ((mMediaPlayer.currentPosition * 100f / mMediaPlayer.duration) + 0.5f).toInt()
            it.onProgress(mMediaPlayer.duration, mMediaPlayer.currentPosition, percentage)
        }
    }
}