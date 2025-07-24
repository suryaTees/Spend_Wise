package com.example.spend_wise

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.spend_wise.ui.theme.Spend_WiseTheme
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        setContent {
            Spend_WiseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: "Unknown"

    val totalBudget = remember { 5000 }
    val totalExpenses = remember { 3200 }
    val remaining = totalBudget - totalExpenses

    var selectedDate by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }

    // Date Picker logic
    fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                calendar.set(year, month, dayOfMonth)
                selectedDate = sdf.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Top Right Calendar Icon Menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select Date"
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All") },
                        onClick = {
                            selectedDate = ""
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Date") },
                        onClick = {
                            openDatePicker()
                            showMenu = false
                        }
                    )
                }
            }
        }

        Text(
            text = "Welcome to Spend Wise!",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Logged in as: $email",
            style = MaterialTheme.typography.bodyMedium
        )

        if (selectedDate.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Filtered by: $selectedDate", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Budget: ₹$totalBudget", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Total Expenses: ₹$totalExpenses", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Remaining: ₹$remaining", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val context = LocalContext.current

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                context.startActivity(Intent(context, AddBudgetActivity::class.java))
            }) {
                Text("Add Budget")
            }

            Button(onClick = {
                context.startActivity(Intent(context, AddExpenseActivity::class.java))
            }) {
                Text("Add Expense")
            }
        }


        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
                (context as? ComponentActivity)?.finish()
            }
        ) {
            Text("Logout")
        }
    }
}
