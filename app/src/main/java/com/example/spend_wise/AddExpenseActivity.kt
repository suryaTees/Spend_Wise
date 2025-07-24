package com.example.spend_wise

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.spend_wise.ui.theme.Spend_WiseTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddExpenseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Spend_WiseTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AddExpenseScreen(onBack = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    var amount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Expense") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Enter expense amount") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (amount.isBlank()) {
                        Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val expenseValue = amount.toIntOrNull()
                    if (expenseValue == null || expenseValue <= 0) {
                        Toast.makeText(context, "Enter a valid positive number", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (user == null) {
                        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    val data = hashMapOf(
                        "amount" to expenseValue,
                        "timestamp" to System.currentTimeMillis(),
                        "userId" to user.uid
                    )

                    db.collection("expenses")
                        .add(data)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Expense saved", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to save expense", Toast.LENGTH_SHORT).show()
                        }
                        .addOnCompleteListener {
                            isLoading = false
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Saving..." else "Save Expense")
            }
        }
    }
}
