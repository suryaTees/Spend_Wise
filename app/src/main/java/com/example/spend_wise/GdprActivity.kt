package com.example.spend_wise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spend_wise.ui.theme.Spend_WiseTheme

class GdprActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Spend_WiseTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GdprScreen(onBack = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GdprScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GDPR Compliance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Your Rights Under GDPR", style = MaterialTheme.typography.headlineSmall)

            Text(
                "As a user, you have the right to access, modify, and request deletion of your personal data.\n\n" +
                        "Spend Wise stores only the data necessary to provide budgeting features, such as your email, budgets, and expenses.\n\n" +
                        "If you wish to access or delete your data, please contact our support team at:\n\nsupport@spendwise.app",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
