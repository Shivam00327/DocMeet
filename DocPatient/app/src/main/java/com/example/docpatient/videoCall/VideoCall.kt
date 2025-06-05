package com.example.docpatient.videoCall

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.docpatient.R

@Composable
fun EasyMeetScreen(meetingId:String) {

    var userName by remember { mutableStateOf("") }
    val isJoinEnabled = meetingId.isNotEmpty() && userName.isNotEmpty()
    val context= LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4A90E2), Color(0xFFC2185B))
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.rm222_mind_20),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Easy Meet",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)

            ) {


                Column(
                    modifier = Modifier.background(Color.White).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = meetingId,
                        onValueChange = {},
                        label = { Text("Meeting ID") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Your name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val intent= Intent(context, ConferenceActivity::class.java)
                            intent.putExtra("MEETING_ID",meetingId)
                            intent.putExtra("USERNAME",userName)
                            context.startActivity(intent)
                        },
                        enabled =isJoinEnabled ,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2185B))
                    ) {
                        Text("Join Meeting")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

//                    Button(
//                        enabled = userName.length>=3,
//                        onClick = {
//                            var randomMeetId=(1..10).map{(0..9).random()}.joinToString("")
//                            val intent= Intent(context, ConferenceActivity::class.java)
//                            intent.putExtra("MEETING_ID",randomMeetId)
//                            intent.putExtra("USERNAME",userName)
//                            context.startActivity(intent)
//
//                                  },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(50.dp),
//                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2185B))
//                    ) {
//                        Text("Create Meeting", color = Color.White)
//                    }
                }
            }
        }
    }
}
