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

class PrivacyPolicyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Spend_WiseTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PrivacyPolicyScreen(onBack = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy") },
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
            Text(
                "Privacy Policy",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                "Spend Wise collects minimal data necessary to provide its core budgeting functionality. We respect your privacy and handle your data with care. We do not sell your personal data or share it with third parties without consent.",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "Data Collected:",
                style = MaterialTheme.typography.titleMedium
            )
            Text("- Email address for login and identity\n" +
                    "- Budget and expense entries for personalized tracking\n" +
                    "- Timestamps for activity tracking",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                "Your Rights:",
                style = MaterialTheme.typography.titleMedium
            )
            Text("- View and delete your data anytime\n" +
                    "- Export your data upon request\n" +
                    "- Delete your account permanently via GDPR section",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                "Data Security:",
                style = MaterialTheme.typography.titleMedium
            )
            Text("We use Firebase Authentication and Firestore which are encrypted and GDPR-compliant platforms to store your data securely.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
