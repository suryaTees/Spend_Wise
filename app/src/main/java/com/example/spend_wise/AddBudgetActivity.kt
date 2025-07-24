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

class AddBudgetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Spend_WiseTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AddBudgetScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen() {
    val context = LocalContext.current
    var amount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Budget") },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? ComponentActivity)?.finish()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Enter budget amount") },
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

                    val budgetValue = amount.toIntOrNull()
                    if (budgetValue == null || budgetValue <= 0) {
                        Toast.makeText(context, "Enter a valid positive number", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (user == null) {
                        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    val data = hashMapOf(
                        "amount" to budgetValue,
                        "timestamp" to System.currentTimeMillis(),
                        "userId" to user.uid
                    )

                    db.collection("budgets")
                        .add(data)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Budget saved", Toast.LENGTH_SHORT).show()
                            (context as? ComponentActivity)?.finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to save budget", Toast.LENGTH_SHORT).show()
                        }
                        .addOnCompleteListener {
                            isLoading = false
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Saving..." else "Save Budget")
            }
        }
    }
}
