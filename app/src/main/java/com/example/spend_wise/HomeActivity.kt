package com.example.spend_wise

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.spend_wise.ui.theme.Spend_WiseTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    val db = FirebaseFirestore.getInstance()
    var selectedDate by remember { mutableStateOf("") }
    var showCalendarMenu by remember { mutableStateOf(false) }
    var showProfileMenu by remember { mutableStateOf(false) }

    var totalBudget by remember { mutableStateOf(0) }
    var totalExpenses by remember { mutableStateOf(0) }

    val sdf = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val coroutineScope = rememberCoroutineScope()

    fun fetchDataForDate(dateFilter: String) {
        if (user == null) return
        coroutineScope.launch {
            try {
                val budgetSnapshot = db.collection("budgets")
                    .whereEqualTo("userId", user.uid)
                    .get()
                    .await()

                val expenseSnapshot = db.collection("expenses")
                    .whereEqualTo("userId", user.uid)
                    .get()
                    .await()

                val filteredBudgets = budgetSnapshot.documents.filter {
                    dateFilter.isBlank() || it.getString("date") == dateFilter
                }
                val filteredExpenses = expenseSnapshot.documents.filter {
                    dateFilter.isBlank() || it.getString("date") == dateFilter
                }

                totalBudget = filteredBudgets.sumOf { (it.getDouble("amount") ?: 0.0).toInt() }
                totalExpenses = filteredExpenses.sumOf { (it.getDouble("amount") ?: 0.0).toInt() }
            } catch (e: Exception) {
                Toast.makeText(context, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchDataForDate(selectedDate)
    }

    fun openDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = sdf.format(calendar.time)
                fetchDataForDate(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val remaining = totalBudget - totalExpenses

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar: Profile icon on left, calendar on right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                IconButton(onClick = { showProfileMenu = true }) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                }

                DropdownMenu(
                    expanded = showProfileMenu,
                    onDismissRequest = { showProfileMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Profile") },
                        onClick = {
                            showProfileMenu = false
                            Toast.makeText(context, "Edit Profile clicked", Toast.LENGTH_SHORT).show()
                            context.startActivity(Intent(context, EditProfileActivity::class.java))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("GDPR Compliance") },
                        onClick = {
                            showProfileMenu = false
                            Toast.makeText(context, "GDPR Compliance clicked", Toast.LENGTH_SHORT).show()
                            context.startActivity(Intent(context, GdprActivity::class.java))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Privacy Policy") },
                        onClick = {
                            showProfileMenu = false
                            Toast.makeText(context, "Privacy Policy clicked", Toast.LENGTH_SHORT).show()
                            context.startActivity(Intent(context, PrivacyPolicyActivity::class.java))
                        }
                    )
                }
            }


            Box {
                IconButton(onClick = { showCalendarMenu = true }) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Calendar")
                }

                DropdownMenu(
                    expanded = showCalendarMenu,
                    onDismissRequest = { showCalendarMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All") },
                        onClick = {
                            selectedDate = ""
                            showCalendarMenu = false
                            fetchDataForDate("")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Select Date") },
                        onClick = {
                            showCalendarMenu = false
                            openDatePicker()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp)) //  Shift content down a bit more

        Text("Welcome to Spend Wise!", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Logged in as: $email", style = MaterialTheme.typography.bodyMedium)

        if (selectedDate.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Filtered by: $selectedDate", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Budget: ₹$totalBudget", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Total Expenses: ₹$totalExpenses", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Remaining: ₹$remaining", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                context.startActivity(Intent(context, AddBudgetActivity::class.java))
            }) { Text("Add Budget") }

            Button(onClick = {
                context.startActivity(Intent(context, AddExpenseActivity::class.java))
            }) { Text("Add Expense") }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, LoginActivity::class.java))
            (context as? ComponentActivity)?.finish()
        }) {
            Text("Logout")
        }
    }
}
