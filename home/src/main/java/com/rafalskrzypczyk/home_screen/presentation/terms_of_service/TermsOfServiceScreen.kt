package com.rafalskrzypczyk.home_screen.presentation.terms_of_service

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.home_screen.presentation.terms_of_service.components.TermsOfServiceTopBar

@Composable
fun TermsOfServiceScreen(
    state: TermsOfServiceState,
    isMandatory: Boolean,
    onEvent: (TermsOfServiceUIEvents) -> Unit,
    onAccepted: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(TermsOfServiceUIEvents.LoadTerms)
    }

    LaunchedEffect(state.isAccepted) {
        if (state.isAccepted) {
            onAccepted()
        }
    }

    Scaffold(
        topBar = {
            TermsOfServiceTopBar(
                title = stringResource(R.string.terms_of_service_title),
                showBackButton = !isMandatory,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(Dimens.DEFAULT_PADDING)
        ) {
            if (state.isLoading) {
                Loading(modifier = Modifier.fillMaxSize())
            } else if (state.error != null) {
                TermsOfServiceErrorState(
                    onRetry = { onEvent(TermsOfServiceUIEvents.LoadTerms) }
                )
            } else {
                state.terms?.let { terms ->
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(Dimens.RADIUS_SMALL)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(Dimens.DEFAULT_PADDING),
                            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
                        ) {
                            item {
                                Text(
                                    text = AnnotatedString.fromHtml(terms.content),
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    if (isMandatory) {
                        Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))

                        ButtonPrimary(
                            title = stringResource(R.string.accept_terms_button),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onEvent(TermsOfServiceUIEvents.AcceptTerms) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TermsOfServiceErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.DEFAULT_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = Icons.Rounded.CloudOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        TextHeadline(
            text = stringResource(R.string.terms_error_title),
            textAlign = TextAlign.Center
        )
        TextPrimary(
            text = stringResource(R.string.terms_error_message),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        ButtonPrimary(
            title = stringResource(R.string.terms_error_retry),
            onClick = onRetry
        )
    }
}
