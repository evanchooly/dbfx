package cn.navigational.dbfx.controls.table

import cn.navigational.dbfx.config.*
import cn.navigational.dbfx.kit.enums.DataType
import cn.navigational.dbfx.tool.svg.SvgImageTranscoder
import cn.navigational.dbfx.kit.model.TableColumnMeta
import javafx.application.Platform
import javafx.beans.property.*
import javafx.collections.ObservableList
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.text.Font

class CustomTableColumn : TableColumn<ObservableList<StringProperty>, String> {
    companion object {
        /**
         * Default column min width
         */
        private const val MIN_COLUMN_WIDTH = 100.0

        /**
         * Default column max width
         */
        private const val MAX_COLUMN_WIDTH = 200.0

        /**
         * Default index column width
         */
        private const val INDEX_COLUMN_WIDTH = 40.0
        private val priKeyIcon = SvgImageTranscoder.svgToImage(PRI_KEY_ICON)
        private val fieldIcon = SvgImageTranscoder.svgToImage(TABLE_FIELD_ICON)
        fun getFieldImage(meta: TableColumnMeta): Image {
            return if (meta.constrainTypes.contains(TableColumnMeta.ConstrainType.PRIMARY_KEY)) {
                priKeyIcon
            } else {
                fieldIcon
            }
        }
    }

    /**
     * Current column is index column?
     */
    private val indexColumn: Boolean

    /**
     *When current column as data column use that constructor
     */
    private val tableColumnMeta: ObjectProperty<TableColumnMeta> = SimpleObjectProperty(null, "tableColumnMeta")

    constructor(column: TableColumnMeta) : this(false) {
        this.updateTableColumn(column)
    }

    constructor(indexColumn: Boolean) : super() {
        if (indexColumn) {
            isSortable = false
            this.minWidth = INDEX_COLUMN_WIDTH
            this.styleClass.add("index-column")
            setCellFactory { IndexTableCell() }
        } else {
            this.styleClass.add("data-column")
            setCellFactory { DataTableCell() }
        }
        this.indexColumn = indexColumn
    }

    fun updateTableColumn(column: TableColumnMeta) {
        val colName = column.colName
        var tip = "$colName:${column.type}"
        if (column.length != null) {
            tip += "(${column.length})"
        }
        this.tableColumnMeta.set(column)
        val label = Label()
        label.tooltip = Tooltip(tip)
        label.graphic = ImageView(getFieldImage(column))
        val mWidth = calColumnWidth(column)
        Platform.runLater {
            this.text = colName
            this.graphic = label
            this.minWidth = mWidth
        }
    }

    private fun calColumnWidth(column: TableColumnMeta): Double {
        val fontSize = Font.getDefault().size
        val title = column.colName
        val iWidth = getFieldImage(column).width
        val tWidth = title.length * fontSize
        val txtWidth = column.length * fontSize
        val temp = iWidth + tWidth
        val mWidth = if (txtWidth > temp) {
            txtWidth
        } else {
            temp
        }
        return when {
            mWidth > MAX_COLUMN_WIDTH -> {
                MAX_COLUMN_WIDTH
            }
            mWidth < MIN_COLUMN_WIDTH -> {
                MIN_COLUMN_WIDTH
            }
            else -> {
                mWidth
            }
        }
    }

    /**
     *
     * Get current column for TableColumnMeta
     *
     */
    fun getTableColumnMeta(): TableColumnMeta {
        if (indexColumn) {
            throw RuntimeException("Current column is index column,so not have TableColumnMeta!")
        }
        return tableColumnMeta.get()
    }
}