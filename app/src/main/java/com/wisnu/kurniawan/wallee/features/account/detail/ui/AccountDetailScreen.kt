package com.wisnu.kurniawan.wallee.features.account.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.wisnu.kurniawan.wallee.R
import com.wisnu.kurniawan.wallee.foundation.extension.getLabel
import com.wisnu.kurniawan.wallee.foundation.extension.getSymbol
import com.wisnu.kurniawan.wallee.foundation.theme.AlphaDisabled
import com.wisnu.kurniawan.wallee.foundation.theme.AlphaHigh
import com.wisnu.kurniawan.wallee.foundation.theme.DividerAlpha
import com.wisnu.kurniawan.wallee.foundation.theme.MediumRadius
import com.wisnu.kurniawan.wallee.foundation.uicomponent.PgBasicTextField
import com.wisnu.kurniawan.wallee.foundation.uicomponent.PgContentTitle
import com.wisnu.kurniawan.wallee.foundation.uicomponent.PgErrorLabel
import com.wisnu.kurniawan.wallee.foundation.uicomponent.PgHeaderEditMode
import com.wisnu.kurniawan.wallee.foundation.uicomponent.PgHeadlineLabel
import com.wisnu.kurniawan.wallee.foundation.uicomponent.PgIcon
import com.wisnu.kurniawan.wallee.foundation.uicomponent.PgPageLayout
import com.wisnu.kurniawan.wallee.foundation.uiextension.collectAsEffectWithLifecycle
import com.wisnu.kurniawan.wallee.foundation.uiextension.paddingCell
import com.wisnu.kurniawan.wallee.runtime.navigation.AccountDetailFlow

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AccountDetailScreen(
    navController: NavController,
    viewModel: AccountDetailViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val effect by viewModel.effect.collectAsEffectWithLifecycle()

    val localFocusManager = LocalFocusManager.current

    when (effect) {
        AccountDetailEffect.ClosePage -> {
            LaunchedEffect(effect) {
                navController.navigateUp()
            }
        }
        null -> {}
    }

    AccountDetailScreen(
        state = state,
        onSaveClick = {
            localFocusManager.clearFocus()
            viewModel.dispatch(AccountDetailAction.Save)
        },
        onCancelClick = {
            localFocusManager.clearFocus()
            navController.navigateUp()
        },
        onCategorySectionClick = {
            localFocusManager.clearFocus()
            navController.navigate(AccountDetailFlow.SelectCategory.route)
        },
        onTotalAmountChange = { viewModel.dispatch(AccountDetailAction.TotalAmountAction.Change(it)) },
        onTotalAmountFocusChange = { viewModel.dispatch(AccountDetailAction.TotalAmountAction.FocusChange(it)) },
        onNameChange = { viewModel.dispatch(AccountDetailAction.NameChange(it)) },
    )
}

@Composable
private fun AccountDetailScreen(
    state: AccountDetailState,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onNameChange: (TextFieldValue) -> Unit,
    onCategorySectionClick: () -> Unit,
    onTotalAmountChange: (TextFieldValue) -> Unit,
    onTotalAmountFocusChange: (Boolean) -> Unit,
) {
    val localFocusManager = LocalFocusManager.current
    PgPageLayout(
        Modifier.fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        localFocusManager.clearFocus()
                    }
                )
            }
    ) {
        PgHeaderEditMode(
            isAllowToSave = state.isValid(),
            title = stringResource(R.string.account_edit_add),
            onSaveClick = onSaveClick,
            onCancelClick = onCancelClick,
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .imePadding()
        ) {
            item {
                if (state.shouldShowDuplicateNameError) {
                    PgErrorLabel(
                        text = stringResource(R.string.account_edit_name_duplicate),
                        modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
                    )
                }

                NameSection(
                    name = state.name,
                    onNameChange = onNameChange,
                    isError = state.shouldShowDuplicateNameError
                )

                CategorySection(
                    categoryName = stringResource(state.selectedAccountType().getLabel()),
                    onCategorySectionClick = onCategorySectionClick
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            item {
                AmountSection(
                    amountItem = state.amountItem,
                    amountSymbol = state.currency.getSymbol() + " ",
                    onTotalAmountChange = onTotalAmountChange,
                    onTotalAmountFocusChange = onTotalAmountFocusChange
                )
            }
        }
    }
}


@Composable
private fun NameSection(
    name: TextFieldValue,
    isError: Boolean,
    onNameChange: (TextFieldValue) -> Unit,
) {
    val titleColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onBackground
    }
    ActionContentCell(
        title = stringResource(R.string.account_edit_name),
        titleColor = titleColor,
        showDivider = true,
        shape = RoundedCornerShape(
            topStart = MediumRadius,
            topEnd = MediumRadius
        ),
        trailing = {
            val focusManager = LocalFocusManager.current
            PgBasicTextField(
                value = name,
                onValueChange = onNameChange,
                keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                placeholderValue = stringResource(R.string.account_edit_name_hint),
                singleLine = true
            )
        },
    )
}

@Composable
private fun CategorySection(
    categoryName: String,
    onCategorySectionClick: () -> Unit,
) {
    ActionContentCell(
        title = stringResource(R.string.category),
        showDivider = false,
        shape = RoundedCornerShape(
            bottomStart = MediumRadius,
            bottomEnd = MediumRadius
        ),
        onClick = onCategorySectionClick,
        trailing = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                PgContentTitle(
                    text = categoryName,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.width(8.dp))
                PgIcon(
                    imageVector = Icons.Rounded.ChevronRight,
                    tint = LocalContentColor.current.copy(alpha = AlphaDisabled)
                )
            }
        },
    )
}

@Composable
private fun AmountSection(
    amountItem: AmountItem,
    amountSymbol: String,
    onTotalAmountChange: (TextFieldValue) -> Unit,
    onTotalAmountFocusChange: (Boolean) -> Unit,
) {
    PgHeadlineLabel(
        text = stringResource(R.string.account_edit_balance),
        modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
    )

    Row(
        modifier = Modifier.background(
            color = MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.medium
        )
            .fillMaxWidth()
            .padding(all = 16.dp)
    ) {
        PgContentTitle(
            text = amountSymbol,
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = if (amountItem.isEditable) {
                    AlphaHigh
                } else {
                    AlphaDisabled
                }
            )
        )
        val localFocusManager = LocalFocusManager.current
        PgBasicTextField(
            value = amountItem.totalAmount,
            onValueChange = onTotalAmountChange,
            modifier = Modifier.onFocusChanged {
                if (amountItem.isEditable) {
                    onTotalAmountFocusChange(it.isFocused)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
            singleLine = true,
            keyboardActions = KeyboardActions(
                onDone = {
                    localFocusManager.clearFocus()
                }
            ),
            enabled = amountItem.isEditable
        )
    }
}

@Composable
private fun ActionContentCell(
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    showDivider: Boolean,
    shape: Shape,
    onClick: (() -> Unit) = {},
    trailing: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.secondary,
        shape = shape,
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingCell()
            ) {
                PgContentTitle(
                    text = title,
                    modifier = Modifier.width(70.dp),
                    color = titleColor
                )
                Spacer(Modifier.size(8.dp))
                trailing()
            }
            if (showDivider) {
                Row {
                    Spacer(
                        Modifier
                            .width(16.dp)
                            .height(1.dp)
                            .background(color = MaterialTheme.colorScheme.secondary)
                    )
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = DividerAlpha))
                }
            }
        }
    }
}
