package cn.godq.applogcat.ui.color


/**
 * @author  GodQ
 * @date  2023/3/6 11:18 上午
 */
class ColorSelector {

    var colorIndex = 0

    private val colorList: List<AlcColor> = AlcColor.values().toList()

    fun nextColor(): AlcColor {
        colorIndex++
        return colorList[colorIndex % colorList.size]
    }
}