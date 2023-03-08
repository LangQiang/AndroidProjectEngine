package cn.godq.applogcat.ui.color


/**
 * @author  GodQ
 * @date  2023/3/6 11:25 上午
 */
enum class AlcColor: IAlcColor {
    COLOR_BLUE {
        override fun getColorStr(): String {
            return "#4a8af5"
        }
    },
    COLOR_RED {
        override fun getColorStr(): String {
            return "#c75450"
        }
    },
    COLOR_YELLOW {
        override fun getColorStr(): String {
            return "#e4b265"
        }
    },
    COLOR_GREEN {
        override fun getColorStr(): String {
            return "#678257"
        }
    },
    COLOR_WHITE {
        override fun getColorStr(): String {
            return "#ffffff"
        }
    },
}