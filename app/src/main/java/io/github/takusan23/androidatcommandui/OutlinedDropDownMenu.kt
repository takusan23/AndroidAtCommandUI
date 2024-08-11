package io.github.takusan23.androidatcommandui

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

/**
 * アウトラインのドロップダウンメニュー
 *
 * @param modifier [Modifier]
 * @param label ラベル
 * @param menuList メニュー一覧
 * @param currentSelectIndex 選択中メニューの位置
 * @param onSelect メニュー押した時
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedDropDownMenu(
    modifier: Modifier = Modifier,
    label: String,
    currentSelectIndex: Int,
    menuList: List<String>,
    onSelect: (Int) -> Unit
) {
    val isExpanded = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = isExpanded.value,
        onExpandedChange = { isExpanded.value = it }
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            maxLines = 1,
            value = menuList[currentSelectIndex],
            onValueChange = { /* do nothing */ },
            label = { Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            textStyle = TextStyle(fontSize = 12.sp)
        )
        ExposedDropdownMenu(
            expanded = isExpanded.value,
            onDismissRequest = { isExpanded.value = false }
        ) {
            menuList.forEachIndexed { index, menu ->
                DropdownMenuItem(
                    onClick = {
                        isExpanded.value = false
                        onSelect(index)
                    },
                    text = {
                        Text(text = menu)
                    }
                )
            }
        }
    }
}