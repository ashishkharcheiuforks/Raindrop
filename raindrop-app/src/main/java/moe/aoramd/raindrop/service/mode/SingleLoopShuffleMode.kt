package moe.aoramd.raindrop.service.mode

/**
 *  looping current song in this mode
 *
 *  @author M.D.
 *  @version dev 1
 */
object SingleLoopShuffleMode : ShuffleMode {

    override val tag: Int = 0x33aaae0

    override fun next(size: Int, currentIndex: Int): Int {
        return if (size <= 0) ShuffleMode.INDEX_NONE
        else {
            when (currentIndex) {
                ShuffleMode.INDEX_NONE -> ShuffleMode.INDEX_NONE
                size - 1 -> 0
                else -> currentIndex + 1
            }
        }
    }

    override fun previous(size: Int, currentIndex: Int): Int {
        return if (size <= 0) ShuffleMode.INDEX_NONE
        else {
            when (currentIndex) {
                ShuffleMode.INDEX_NONE -> ShuffleMode.INDEX_NONE
                0 -> size - 1
                else -> currentIndex - 1
            }
        }
    }

    override fun nextAuto(size: Int, currentIndex: Int): Int = currentIndex
}